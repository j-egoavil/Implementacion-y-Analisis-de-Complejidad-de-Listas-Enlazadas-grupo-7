from __future__ import annotations

from pathlib import Path
from typing import Iterable

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from pandas.errors import EmptyDataError


SCRIPT_DIR = Path(__file__).resolve().parent
ROOT_DIR = SCRIPT_DIR.parent
DATA_DIR = ROOT_DIR / "data"
OUTPUT_DIR = ROOT_DIR / "docs" / "graficas" / "avanzadas"

SLOPE_DIR = OUTPUT_DIR / "pendiente-loglog"
SLOPE_STACK_DIR = SLOPE_DIR / "stack"
SLOPE_QUEUE_DIR = SLOPE_DIR / "queue"
SLOPE_LIST_SINGLY_DIR = SLOPE_DIR / "list" / "singly"
SLOPE_LIST_DOUBLY_DIR = SLOPE_DIR / "list" / "doubly"
SPEEDUP_DIR = OUTPUT_DIR / "speedup-tail"
COLOR_LINES_DIR = OUTPUT_DIR / "lineas-por-operacion"
COLOR_LINES_STACK_DIR = COLOR_LINES_DIR / "stack"
COLOR_LINES_QUEUE_DIR = COLOR_LINES_DIR / "queue"
COLOR_LINES_LIST_DIR = COLOR_LINES_DIR / "list"
HEATMAP_DIR = OUTPUT_DIR / "heatmap"
HEATMAP_STACK_DIR = HEATMAP_DIR / "stack"
HEATMAP_QUEUE_DIR = HEATMAP_DIR / "queue"
HEATMAP_LIST_DIR = HEATMAP_DIR / "list"
HEATMAP_GLOBAL_DIR = HEATMAP_DIR / "global"

LIST_OPS = [
    "push_front",
    "push_back",
    "pop_front",
    "pop_back",
    "find",
    "erase",
    "add_before",
    "add_after",
]

TARGET_SIZES = [10, 100, 1_000, 10_000, 100_000, 1_000_000]


def y_column(df: pd.DataFrame) -> str:
    if "avg_time_ns" in df.columns:
        return "avg_time_ns"
    return "time"


def read_csv(path: Path) -> pd.DataFrame | None:
    if not path.exists() or path.stat().st_size == 0:
        return None
    try:
        df = pd.read_csv(path)
    except EmptyDataError:
        return None
    if "size" not in df.columns:
        return None
    y_col = y_column(df)
    if y_col not in df.columns:
        return None
    out = df[["size", y_col]].copy()
    out = out.rename(columns={y_col: "time_ns"})
    out = out.dropna()
    if out.empty:
        return None
    return out


def pick_time_unit(max_ns: float) -> tuple[str, float]:
    if max_ns >= 1_000_000:
        return "ms", 1_000_000.0
    if max_ns >= 1_000:
        return "us", 1_000.0
    return "ns", 1.0


def estimate_missing_sizes(df: pd.DataFrame, target_sizes: list[int]) -> tuple[pd.DataFrame, set[int]]:
    out = df.copy()
    existing = set(int(x) for x in out["size"].tolist())
    estimated: set[int] = set()

    if len(out) < 2:
        return out.sort_values("size"), estimated

    x_log = np.log10(out["size"].astype(float).to_numpy())
    y_log = np.log10(out["time_ns"].astype(float).to_numpy())
    alpha, intercept = np.polyfit(x_log, y_log, 1)

    rows = []
    for n in target_sizes:
        if n in existing:
            continue
        pred = (float(n) ** float(alpha)) * (10 ** float(intercept))
        pred = max(pred, 1.0)
        rows.append({"size": n, "time_ns": pred})
        estimated.add(n)

    if rows:
        out = pd.concat([out, pd.DataFrame(rows)], ignore_index=True)

    return out.sort_values("size"), estimated


def clear_pngs(folders: Iterable[Path]) -> None:
    for folder in folders:
        folder.mkdir(parents=True, exist_ok=True)
        for png in folder.rglob("*.png"):
            png.unlink()


def slope_output_path(label: str) -> Path:
    file_name = f"slope_{label}.png"
    if label.startswith("stack_"):
        return SLOPE_STACK_DIR / file_name
    if label.startswith("queue_"):
        return SLOPE_QUEUE_DIR / file_name
    if label.startswith("singly"):
        return SLOPE_LIST_SINGLY_DIR / file_name
    if label.startswith("doubly"):
        return SLOPE_LIST_DOUBLY_DIR / file_name
    return SLOPE_DIR / file_name


def stack_paths() -> dict[str, Path]:
    base = DATA_DIR / "data-stack"
    return {
        "push": base / "stack_push.csv",
        "pop": base / "stack_pop.csv",
        "peek": base / "stack_peek.csv",
        "delete": base / "stack_delete.csv",
    }


def queue_paths() -> dict[str, Path]:
    base = DATA_DIR / "data-queue"
    return {
        "enqueue": base / "queue_enqueue.csv",
        "dequeue": base / "queue_dequeue.csv",
        "front": base / "queue_front.csv",
        "delete": base / "queue_delete.csv",
    }


def list_impl_paths() -> dict[str, Path]:
    base = DATA_DIR / "data-list"
    out: dict[str, Path] = {}
    for op in LIST_OPS:
        out[f"singly_no_tail_{op}"] = base / "list-singly" / "no-tail" / f"list_singly_{op}.csv"
        out[f"singly_tail_{op}"] = base / "list-singly" / "whit-tail" / f"list_singly_tail_{op}.csv"
        out[f"doubly_no_tail_{op}"] = base / "list-doubly" / "no-tail" / f"list_doubly_{op}.csv"
        out[f"doubly_tail_{op}"] = base / "list-doubly" / "whit-tail" / f"list_doubly_tail_{op}.csv"
    return out


def plot_lines_by_operation(
    title: str,
    op_to_path: dict[str, Path],
    output_path: Path,
    align_common_sizes: bool = False,
    fill_target_sizes: list[int] | None = None,
) -> None:
    plt.figure(figsize=(10, 5))
    plotted = 0
    loaded: dict[str, pd.DataFrame] = {}

    estimated_sizes_by_op: dict[str, set[int]] = {}

    for op, path in op_to_path.items():
        df = read_csv(path)
        if df is None:
            continue
        df = df.sort_values("size")
        if fill_target_sizes is not None:
            df, estimated = estimate_missing_sizes(df, fill_target_sizes)
            estimated_sizes_by_op[op] = estimated
        loaded[op] = df

    if not loaded:
        plt.close()
        print(f"Skip: {output_path.name} (no data)")
        return

    common_sizes: set[int] | None = None
    if align_common_sizes:
        for df in loaded.values():
            sizes = set(int(x) for x in df["size"].tolist())
            common_sizes = sizes if common_sizes is None else common_sizes.intersection(sizes)
        if not common_sizes:
            plt.close()
            print(f"Skip: {output_path.name} (no common sizes)")
            return

    max_ns = max(float(df["time_ns"].max()) for df in loaded.values())
    unit, divisor = pick_time_unit(max_ns)

    for op, df in loaded.items():
        plot_df = df
        if common_sizes is not None:
            plot_df = df[df["size"].isin(sorted(common_sizes))]
            if plot_df.empty:
                continue

        line, = plt.plot(plot_df["size"], plot_df["time_ns"] / divisor, marker="o", label=op)

        estimated_sizes = estimated_sizes_by_op.get(op, set())
        if estimated_sizes:
            est_df = plot_df[plot_df["size"].isin(sorted(estimated_sizes))]
            if not est_df.empty:
                plt.scatter(
                    est_df["size"],
                    est_df["time_ns"] / divisor,
                    marker="x",
                    color=line.get_color(),
                    s=65,
                )
        plotted += 1

    if plotted == 0:
        plt.close()
        print(f"Skip: {output_path.name} (no data)")
        return

    plt.xlabel("Input size")
    plt.ylabel(f"Time ({unit})")
    plt.title(title)
    plt.xscale("log")
    plt.yscale("log")
    plt.grid(alpha=0.25)
    plt.legend(ncol=2)
    if fill_target_sizes is not None:
        plt.figtext(0.99, 0.01, "x = estimated missing size", ha="right", fontsize=8)
    plt.tight_layout()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=170)
    plt.close()
    print(f"Saved: {output_path}")


def slope_from_df(df: pd.DataFrame) -> tuple[float, float]:
    x = np.log10(df["size"].astype(float).to_numpy())
    y = np.log10(df["time_ns"].astype(float).to_numpy())
    alpha, intercept = np.polyfit(x, y, 1)
    return float(alpha), float(intercept)


def plot_loglog_with_slope(label: str, csv_path: Path, output_path: Path) -> None:
    df = read_csv(csv_path)
    if df is None or len(df) < 2:
        print(f"Skip: {output_path.name} (not enough data)")
        return

    alpha, intercept = slope_from_df(df)
    x = df["size"].astype(float).to_numpy()
    y_fit = (x ** alpha) * (10 ** intercept)
    unit, divisor = pick_time_unit(float(df["time_ns"].max()))

    plt.figure(figsize=(8, 5))
    plt.plot(df["size"], df["time_ns"] / divisor, marker="o", label="Measured")
    plt.plot(df["size"], y_fit / divisor, linestyle="--", label=f"Fit alpha={alpha:.2f}")
    plt.xlabel("Input size")
    plt.ylabel(f"Time ({unit})")
    plt.title(f"Log-log slope: {label}")
    plt.xscale("log")
    plt.yscale("log")
    plt.grid(alpha=0.25)
    plt.legend()
    plt.tight_layout()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=170)
    plt.close()
    print(f"Saved: {output_path}")


def plot_speedup_tail_vs_no_tail(
    family: str,
    op: str,
    no_tail_csv: Path,
    tail_csv: Path,
    output_path: Path,
) -> None:
    a = read_csv(no_tail_csv)
    b = read_csv(tail_csv)
    if a is None or b is None:
        print(f"Skip: {output_path.name} (missing data)")
        return

    merged = a.merge(b, on="size", suffixes=("_no_tail", "_tail"))
    if merged.empty:
        print(f"Skip: {output_path.name} (no common sizes)")
        return

    merged["speedup"] = merged["time_ns_no_tail"] / merged["time_ns_tail"]

    plt.figure(figsize=(8, 5))
    plt.plot(merged["size"], merged["speedup"], marker="o")
    plt.axhline(1.0, color="gray", linestyle="--", linewidth=1)
    plt.xlabel("Input size")
    plt.ylabel("Speedup (no-tail / tail)")
    plt.title(f"Speedup {family}: {op}")
    plt.xscale("log")
    plt.grid(alpha=0.25)
    plt.tight_layout()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=170)
    plt.close()
    print(f"Saved: {output_path}")


def plot_heatmap_operations_vs_size(title: str, op_to_path: dict[str, Path], output_path: Path) -> None:
    rows: list[pd.Series] = []
    labels: list[str] = []
    sizes_reference: list[int] | None = None

    for op, path in op_to_path.items():
        df = read_csv(path)
        if df is None:
            continue
        df = df.sort_values("size")
        sizes = df["size"].astype(int).tolist()
        if sizes_reference is None:
            sizes_reference = sizes
        if sizes_reference != sizes:
            continue
        rows.append(np.log10(df["time_ns"].to_numpy(dtype=float) + 1.0))
        labels.append(op)

    if not rows or sizes_reference is None:
        print(f"Skip: {output_path.name} (no aligned data)")
        return

    matrix = np.vstack(rows)

    plt.figure(figsize=(11, 4))
    im = plt.imshow(matrix, aspect="auto", cmap="viridis")
    plt.colorbar(im, label="log10(time_ns + 1)")
    plt.yticks(ticks=np.arange(len(labels)), labels=labels)
    plt.xticks(ticks=np.arange(len(sizes_reference)), labels=[str(s) for s in sizes_reference], rotation=45)
    plt.xlabel("Input size")
    plt.ylabel("Operation")
    plt.title(title)
    plt.tight_layout()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=170)
    plt.close()
    print(f"Saved: {output_path}")


def plot_list_global_heatmap_max_n(output_path: Path) -> None:
    impls = [
        "singly_no_tail",
        "singly_tail",
        "doubly_no_tail",
        "doubly_tail",
    ]

    table: list[list[float]] = []
    labels: list[str] = []

    for impl in impls:
        row: list[float] = []
        valid = True
        for op in LIST_OPS:
            path = list_impl_paths()[f"{impl}_{op}"]
            df = read_csv(path)
            if df is None:
                valid = False
                break
            max_idx = df["size"].idxmax()
            row.append(float(df.loc[max_idx, "time_ns"]))
        if valid:
            table.append(row)
            labels.append(impl)

    if not table:
        print(f"Skip: {output_path.name} (no data)")
        return

    matrix = np.log10(np.array(table, dtype=float) + 1.0)

    plt.figure(figsize=(12, 4.8))
    im = plt.imshow(matrix, aspect="auto", cmap="magma")
    plt.colorbar(im, label="log10(time_ns + 1)")
    plt.yticks(ticks=np.arange(len(labels)), labels=labels)
    plt.xticks(ticks=np.arange(len(LIST_OPS)), labels=LIST_OPS, rotation=35, ha="right")
    plt.xlabel("Operation")
    plt.ylabel("Implementation")
    plt.title("List global heatmap at max n")
    plt.tight_layout()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=170)
    plt.close()
    print(f"Saved: {output_path}")


def main() -> None:
    clear_pngs([SLOPE_DIR, SPEEDUP_DIR, COLOR_LINES_DIR, HEATMAP_DIR])

    stack = stack_paths()
    queue = queue_paths()

    # User idea: y=time, x=size, color=operation.
    plot_lines_by_operation(
        title="Stack methods by operation",
        op_to_path=stack,
        output_path=COLOR_LINES_STACK_DIR / "stack_methods_color_by_operation.png",
        align_common_sizes=True,
        fill_target_sizes=TARGET_SIZES,
    )
    plot_lines_by_operation(
        title="Queue methods by operation",
        op_to_path=queue,
        output_path=COLOR_LINES_QUEUE_DIR / "queue_methods_color_by_operation.png",
        align_common_sizes=True,
        fill_target_sizes=TARGET_SIZES,
    )

    for impl in ["singly_no_tail", "singly_tail", "doubly_no_tail", "doubly_tail"]:
        op_paths = {op: list_impl_paths()[f"{impl}_{op}"] for op in LIST_OPS}
        plot_lines_by_operation(
            title=f"List methods by operation: {impl}",
            op_to_path=op_paths,
            output_path=COLOR_LINES_LIST_DIR / f"list_methods_color_by_operation_{impl}.png",
            align_common_sizes=True,
            fill_target_sizes=TARGET_SIZES,
        )

    # Heatmaps.
    plot_heatmap_operations_vs_size(
        title="Heatmap stack: operation vs size",
        op_to_path=stack,
        output_path=HEATMAP_STACK_DIR / "heatmap_stack_operation_vs_size.png",
    )
    plot_heatmap_operations_vs_size(
        title="Heatmap queue: operation vs size",
        op_to_path=queue,
        output_path=HEATMAP_QUEUE_DIR / "heatmap_queue_operation_vs_size.png",
    )
    for impl in ["singly_no_tail", "singly_tail", "doubly_no_tail", "doubly_tail"]:
        op_paths = {op: list_impl_paths()[f"{impl}_{op}"] for op in LIST_OPS}
        plot_heatmap_operations_vs_size(
            title=f"Heatmap list ({impl}): operation vs size",
            op_to_path=op_paths,
            output_path=HEATMAP_LIST_DIR / f"heatmap_list_{impl}_operation_vs_size.png",
        )
    plot_list_global_heatmap_max_n(HEATMAP_GLOBAL_DIR / "heatmap_list_global_max_n.png")

    # Log-log slopes for all methods.
    all_series: dict[str, Path] = {}
    all_series.update({f"stack_{k}": v for k, v in stack.items()})
    all_series.update({f"queue_{k}": v for k, v in queue.items()})
    all_series.update(list_impl_paths())

    for label, csv_path in all_series.items():
        plot_loglog_with_slope(
            label=label,
            csv_path=csv_path,
            output_path=slope_output_path(label),
        )

    # Speedup tail vs no-tail for list families.
    for op in LIST_OPS:
        plot_speedup_tail_vs_no_tail(
            family="singly",
            op=op,
            no_tail_csv=list_impl_paths()[f"singly_no_tail_{op}"],
            tail_csv=list_impl_paths()[f"singly_tail_{op}"],
            output_path=SPEEDUP_DIR / "singly" / f"speedup_singly_{op}.png",
        )
        plot_speedup_tail_vs_no_tail(
            family="doubly",
            op=op,
            no_tail_csv=list_impl_paths()[f"doubly_no_tail_{op}"],
            tail_csv=list_impl_paths()[f"doubly_tail_{op}"],
            output_path=SPEEDUP_DIR / "doubly" / f"speedup_doubly_{op}.png",
        )


if __name__ == "__main__":
    main()
