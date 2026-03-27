from pathlib import Path

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from pandas.errors import EmptyDataError


SCRIPT_DIR = Path(__file__).resolve().parent
DATA_DIR = SCRIPT_DIR.parent / "data"
OUTPUT_DIR = SCRIPT_DIR.parent / "docs" / "graficas" / "comparaciones_avanzadas"

COLOR_LINES_DIR = OUTPUT_DIR / "lineas-por-operacion"
COLOR_LINES_STACK_DIR = COLOR_LINES_DIR / "stack"
COLOR_LINES_QUEUE_DIR = COLOR_LINES_DIR / "queue"
COLOR_LINES_LIST_DIR = COLOR_LINES_DIR / "list"

HEATMAP_DIR = OUTPUT_DIR / "heatmaps"
SLOPE_DIR = OUTPUT_DIR / "slopes-loglog"
SPEEDUP_DIR = OUTPUT_DIR / "speedups"

TARGET_SIZES = [10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000]

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

KEY_SPEEDUP_OPS = [
    "push_back",
    "pop_back",
    "find",
]


def read_csv(path: Path) -> pd.DataFrame | None:
    if not path.exists() or path.stat().st_size == 0:
        return None
    try:
        df = pd.read_csv(path)
    except EmptyDataError:
        return None

    if "size" not in df.columns:
        return None

    if "median_ns" in df.columns:
        df = df.rename(columns={"median_ns": "time_ns"})
    elif "avg_time_ns" in df.columns:
        df = df.rename(columns={"avg_time_ns": "time_ns"})
    elif "time" in df.columns:
        df = df.rename(columns={"time": "time_ns"})
    else:
        return None

    return df[["size", "time_ns"]].dropna().sort_values("size")


def pretty_impl_name(name: str) -> str:
    mapping = {
        "singly_no_tail": "Singly sin tail",
        "singly_tail": "Singly con tail",
        "doubly_no_tail": "Doubly sin tail",
        "doubly_tail": "Doubly con tail",
    }
    return mapping.get(name, name)


def pretty_op_name(op: str) -> str:
    mapping = {
        "push_front": "push_front",
        "push_back": "push_back",
        "pop_front": "pop_front",
        "pop_back": "pop_back",
        "find": "find",
        "erase": "erase",
        "add_before": "add_before",
        "add_after": "add_after",
        "push": "push",
        "pop": "pop",
        "peek": "peek",
        "enqueue": "enqueue",
        "dequeue": "dequeue",
        "front": "front",
        "delete": "delete",
    }
    return mapping.get(op, op)


def pick_time_unit(max_ns: float) -> tuple[str, float]:
    if max_ns >= 1_000_000_000:
        return "s", 1_000_000_000
    if max_ns >= 1_000_000:
        return "ms", 1_000_000
    if max_ns >= 1_000:
        return "µs", 1_000
    return "ns", 1.0


def estimate_missing_sizes(df: pd.DataFrame, target_sizes: list[int]) -> tuple[pd.DataFrame, set[int]]:
    if df is None or df.empty:
        return df, set()

    existing_sizes = set(int(x) for x in df["size"].tolist())
    missing = [s for s in target_sizes if s not in existing_sizes]
    if not missing:
        return df, set()

    if len(df) < 2:
        return df, set()

    x = np.log10(df["size"].astype(float).values)
    y = np.log10(df["time_ns"].astype(float).values)

    slope, intercept = np.polyfit(x, y, 1)

    estimated_rows = []
    for s in missing:
        pred_log_y = slope * np.log10(float(s)) + intercept
        pred_y = float(10 ** pred_log_y)
        estimated_rows.append({"size": s, "time_ns": pred_y})

    estimated_df = pd.DataFrame(estimated_rows)
    out = pd.concat([df, estimated_df], ignore_index=True).sort_values("size")
    return out, set(missing)


def plot_lines_by_operation(
        title: str,
        op_to_path: dict[str, Path],
        output_path: Path,
        align_common_sizes: bool = False,
        fill_target_sizes: list[int] | None = None,
        show_estimated_note: bool = False,
) -> None:
    plt.figure(figsize=(10, 5.6))
    plotted = 0
    loaded: dict[str, pd.DataFrame] = {}
    estimated_sizes_by_op: dict[str, set[int]] = {}

    for op, path in op_to_path.items():
        df = read_csv(path)
        if df is None:
            continue

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

        line, = plt.plot(
            plot_df["size"],
            plot_df["time_ns"] / divisor,
            marker="o",
            linewidth=1.8,
            markersize=4.5,
            label=pretty_op_name(op),
            )

        estimated_sizes = estimated_sizes_by_op.get(op, set())
        if estimated_sizes:
            est_df = plot_df[plot_df["size"].isin(sorted(estimated_sizes))]
            if not est_df.empty:
                plt.scatter(
                    est_df["size"],
                    est_df["time_ns"] / divisor,
                    marker="x",
                    color=line.get_color(),
                    s=55,
                    )

        plotted += 1

    if plotted == 0:
        plt.close()
        print(f"Skip: {output_path.name} (no data)")
        return

    plt.xlabel("Tamaño de entrada (n)")
    plt.ylabel(f"Tiempo ({unit})")
    plt.title(title)
    plt.xscale("log")
    plt.yscale("log")
    plt.grid(alpha=0.18, which="both")
    plt.legend(ncol=2, fontsize=8)

    plt.tight_layout()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=180, bbox_inches="tight")
    plt.close()
    print(f"Saved: {output_path}")


def plot_heatmap(title: str, op_to_path: dict[str, Path], output_path: Path) -> None:
    frames = []
    for op, path in op_to_path.items():
        df = read_csv(path)
        if df is None:
            continue
        temp = df.copy()
        temp["operation"] = pretty_op_name(op)
        frames.append(temp)

    if not frames:
        print(f"Skip: {output_path.name} (no data)")
        return

    full = pd.concat(frames, ignore_index=True)
    pivot = full.pivot(index="operation", columns="size", values="time_ns")
    if pivot.empty:
        print(f"Skip: {output_path.name} (empty pivot)")
        return

    z = np.log10(pivot.astype(float))

    fig, ax = plt.subplots(figsize=(10, 4.8))
    im = ax.imshow(z.values, aspect="auto", interpolation="nearest")

    ax.set_xticks(range(len(z.columns)))
    ax.set_xticklabels([str(c) for c in z.columns], rotation=45, ha="right")
    ax.set_yticks(range(len(z.index)))
    ax.set_yticklabels(list(z.index))
    ax.set_xlabel("Tamaño de entrada (n)")
    ax.set_ylabel("Operación")
    ax.set_title(title)

    cbar = fig.colorbar(im, ax=ax)
    cbar.set_label("log10(tiempo en ns)")

    plt.tight_layout()
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=180, bbox_inches="tight")
    plt.close()
    print(f"Saved: {output_path}")


def plot_loglog_with_slope(label: str, csv_path: Path, output_path: Path) -> None:
    df = read_csv(csv_path)
    if df is None or len(df) < 2:
        print(f"Skip: {output_path.name} (insufficient data)")
        return

    x = np.log10(df["size"].astype(float).values)
    y = np.log10(df["time_ns"].astype(float).values)

    slope, intercept = np.polyfit(x, y, 1)
    fit_y = slope * x + intercept

    plt.figure(figsize=(7.2, 5))
    plt.plot(x, y, marker="o", label="Datos")
    plt.plot(x, fit_y, linestyle="--", label=f"Ajuste lineal (α≈{slope:.2f})")
    plt.xlabel("log10(n)")
    plt.ylabel("log10(tiempo en ns)")
    plt.title(f"Pendiente log-log: {label}")
    plt.grid(alpha=0.18, which="both")
    plt.legend(fontsize=9)
    plt.tight_layout()

    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=180, bbox_inches="tight")
    plt.close()
    print(f"Saved: {output_path}")


def plot_speedup(
        title: str,
        faster_path: Path,
        slower_path: Path,
        output_path: Path,
        faster_label: str,
        slower_label: str,
) -> None:
    fast_df = read_csv(faster_path)
    slow_df = read_csv(slower_path)

    if fast_df is None or slow_df is None:
        print(f"Skip: {output_path.name} (missing data)")
        return

    merged = pd.merge(
        fast_df.rename(columns={"time_ns": "fast_ns"}),
        slow_df.rename(columns={"time_ns": "slow_ns"}),
        on="size",
        how="inner",
    )

    if merged.empty:
        print(f"Skip: {output_path.name} (no shared sizes)")
        return

    merged["speedup"] = merged["slow_ns"] / merged["fast_ns"]

    plt.figure(figsize=(8.2, 5))
    plt.plot(merged["size"], merged["speedup"], marker="o")
    plt.axhline(1.0, linestyle="--", linewidth=1)
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("Tamaño de entrada (n)")
    plt.ylabel(f"Speedup ({slower_label} / {faster_label})")
    plt.title(title)
    plt.grid(alpha=0.18, which="both")
    plt.tight_layout()

    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=180, bbox_inches="tight")
    plt.close()
    print(f"Saved: {output_path}")


def path_list_impl(impl: str, op: str) -> Path:
    base = DATA_DIR / "data-list"
    mapping = {
        "singly_no_tail": base / "list-singly" / "no-tail" / f"list_singly_{op}.csv",
        "singly_tail": base / "list-singly" / "whit-tail" / f"list_singly_tail_{op}.csv",
        "doubly_no_tail": base / "list-doubly" / "no-tail" / f"list_doubly_{op}.csv",
        "doubly_tail": base / "list-doubly" / "whit-tail" / f"list_doubly_tail_{op}.csv",
    }
    return mapping[impl]


def path_stack(op: str) -> Path:
    return DATA_DIR / "data-stack" / f"stack_{op}.csv"


def path_queue(op: str) -> Path:
    return DATA_DIR / "data-queue" / f"queue_{op}.csv"


def clear_existing_pngs() -> None:
    if not OUTPUT_DIR.exists():
        return
    for png in OUTPUT_DIR.rglob("*.png"):
        png.unlink()


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    clear_existing_pngs()

    # -------- Gráficas principales: líneas por operación --------

    stack = {
        "push": path_stack("push"),
        "pop": path_stack("pop"),
        "peek": path_stack("peek"),
        "delete": path_stack("delete"),
    }
    queue = {
        "enqueue": path_queue("enqueue"),
        "dequeue": path_queue("dequeue"),
        "front": path_queue("front"),
        "delete": path_queue("delete"),
    }

    list_impls = {
        "singly_no_tail": {op: path_list_impl("singly_no_tail", op) for op in LIST_OPS},
        "singly_tail": {op: path_list_impl("singly_tail", op) for op in LIST_OPS},
        "doubly_no_tail": {op: path_list_impl("doubly_no_tail", op) for op in LIST_OPS},
        "doubly_tail": {op: path_list_impl("doubly_tail", op) for op in LIST_OPS},
    }

    plot_lines_by_operation(
        title="Métodos de stack por operación",
        op_to_path=stack,
        output_path=COLOR_LINES_STACK_DIR / "stack_methods_color_by_operation.png",
        align_common_sizes=True,
        fill_target_sizes=None,
    )

    plot_lines_by_operation(
        title="Métodos de queue por operación",
        op_to_path=queue,
        output_path=COLOR_LINES_QUEUE_DIR / "queue_methods_color_by_operation.png",
        align_common_sizes=True,
        fill_target_sizes=None,
    )

    for impl, op_paths in list_impls.items():
        plot_lines_by_operation(
            title=f"Métodos de lista: {pretty_impl_name(impl)}",
            op_to_path=op_paths,
            output_path=COLOR_LINES_LIST_DIR / f"list_methods_color_by_operation_{impl}.png",
            align_common_sizes=True,
            fill_target_sizes=None,
        )

    # -------- Heatmaps (apoyo / anexo) --------

    plot_heatmap("Mapa de calor: stack", stack, HEATMAP_DIR / "heatmap_stack.png")
    plot_heatmap("Mapa de calor: queue", queue, HEATMAP_DIR / "heatmap_queue.png")

    for impl, op_paths in list_impls.items():
        plot_heatmap(
            f"Mapa de calor: {pretty_impl_name(impl)}",
            op_paths,
            HEATMAP_DIR / f"heatmap_{impl}.png",
            )

    # -------- Pendientes log-log --------

    for op, path in stack.items():
        plot_loglog_with_slope(
            f"Stack {pretty_op_name(op)}",
            path,
            SLOPE_DIR / "stack" / f"slope_stack_{op}.png",
            )

    for op, path in queue.items():
        plot_loglog_with_slope(
            f"Queue {pretty_op_name(op)}",
            path,
            SLOPE_DIR / "queue" / f"slope_queue_{op}.png",
            )

    for impl, op_paths in list_impls.items():
        for op, path in op_paths.items():
            plot_loglog_with_slope(
                f"{pretty_impl_name(impl)} - {pretty_op_name(op)}",
                path,
                SLOPE_DIR / "list" / impl / f"slope_{impl}_{op}.png",
                )

    # -------- Speedups clave --------

    for op in KEY_SPEEDUP_OPS:
        plot_speedup(
            title=f"Speedup Singly con tail vs sin tail: {pretty_op_name(op)}",
            faster_path=path_list_impl("singly_tail", op),
            slower_path=path_list_impl("singly_no_tail", op),
            output_path=SPEEDUP_DIR / "singly" / f"speedup_singly_{op}.png",
            faster_label="con tail",
            slower_label="sin tail",
        )

        plot_speedup(
            title=f"Speedup Doubly con tail vs sin tail: {pretty_op_name(op)}",
            faster_path=path_list_impl("doubly_tail", op),
            slower_path=path_list_impl("doubly_no_tail", op),
            output_path=SPEEDUP_DIR / "doubly" / f"speedup_doubly_{op}.png",
            faster_label="con tail",
            slower_label="sin tail",
        )

    print("Done. All advanced charts generated.")


if __name__ == "__main__":
    main()