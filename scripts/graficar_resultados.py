from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd
from pandas.errors import EmptyDataError


SCRIPT_DIR = Path(__file__).resolve().parent
DATA_DIR = SCRIPT_DIR.parent / "data"
OUTPUT_DIR = SCRIPT_DIR.parent / "docs" / "graficas" / "individuales"
LIST_DIR = OUTPUT_DIR / "list"
STACK_DIR = OUTPUT_DIR / "stack"
QUEUE_DIR = OUTPUT_DIR / "queue"


def y_column(df: pd.DataFrame) -> str:
    if "median_ns" in df.columns:
        return "median_ns"
    if "avg_time_ns" in df.columns:
        return "avg_time_ns"
    return "time"


def discover_csv_files() -> list[Path]:
    return sorted(DATA_DIR.rglob("*.csv"))


def output_path_for_csv(csv_path: Path) -> Path:
    relative = csv_path.relative_to(DATA_DIR)
    stem = csv_path.stem

    if relative.parts[0] == "data-list":
        structure_family = relative.parts[1]
        tail_folder = relative.parts[2]

        if structure_family == "list-singly":
            return LIST_DIR / "singly" / tail_folder / f"{stem}.png"

        if structure_family == "list-doubly":
            return LIST_DIR / "doubly" / tail_folder / f"{stem}.png"

        return LIST_DIR / tail_folder / f"{stem}.png"

    if relative.parts[0] == "data-stack":
        return STACK_DIR / f"{stem}.png"

    if relative.parts[0] == "data-queue":
        return QUEUE_DIR / f"{stem}.png"

    safe_name = "__".join(relative.with_suffix("").parts)
    return OUTPUT_DIR / f"{safe_name}.png"


def pretty_stem(stem: str) -> str:
    replacements = {
        "list_singly_": "Lista singly: ",
        "list_singly_tail_": "Lista singly con tail: ",
        "list_doubly_": "Lista doubly: ",
        "list_doubly_tail_": "Lista doubly con tail: ",
        "stack_": "Stack: ",
        "queue_": "Queue: ",
        "_": " ",
    }

    title = stem
    for old, new in replacements.items():
        title = title.replace(old, new)

    return title


def pick_time_unit(max_ns: float) -> tuple[str, float]:
    if max_ns >= 1_000_000_000:
        return "s", 1_000_000_000
    if max_ns >= 1_000_000:
        return "ms", 1_000_000
    if max_ns >= 1_000:
        return "µs", 1_000
    return "ns", 1.0


def read_csv_safely(csv_path: Path) -> pd.DataFrame | None:
    if not csv_path.exists() or csv_path.stat().st_size == 0:
        print(f"Skip empty CSV: {csv_path}")
        return None

    try:
        data = pd.read_csv(csv_path)
    except EmptyDataError:
        print(f"Skip empty CSV: {csv_path}")
        return None

    if "size" not in data.columns:
        print(f"Skip (no size column): {csv_path}")
        return None

    y_col = y_column(data)
    if y_col not in data.columns:
        print(f"Skip (no time column): {csv_path}")
        return None

    cleaned = data[["size", y_col]].dropna()
    if cleaned.empty:
        print(f"Skip (no usable rows): {csv_path}")
        return None

    cleaned = cleaned.sort_values("size")
    return cleaned.rename(columns={y_col: "time_ns"})


def plot_csv(csv_path: Path) -> None:
    data = read_csv_safely(csv_path)
    if data is None:
        return

    max_ns = float(data["time_ns"].max())
    unit, divisor = pick_time_unit(max_ns)

    plt.figure(figsize=(8.5, 5.2))
    plt.plot(
        data["size"],
        data["time_ns"] / divisor,
        marker="o",
        linewidth=1.8,
        markersize=4.5,
        )
    plt.xlabel("Tamaño de entrada (n)")
    plt.ylabel(f"Tiempo ({unit})")
    plt.title(pretty_stem(csv_path.stem))
    plt.xscale("log")
    plt.yscale("log")
    plt.grid(alpha=0.18, which="both")
    plt.tight_layout()

    output_path = output_path_for_csv(csv_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=180, bbox_inches="tight")
    plt.close()

    print(f"Saved: {output_path}")


def clear_existing_pngs() -> None:
    for folder in [LIST_DIR, STACK_DIR, QUEUE_DIR]:
        if not folder.exists():
            continue
        for png in folder.rglob("*.png"):
            png.unlink()


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    clear_existing_pngs()

    csv_files = discover_csv_files()
    if not csv_files:
        print(f"No CSV files found in {DATA_DIR}")
        return

    for csv_path in csv_files:
        plot_csv(csv_path)

    print(f"Generated {len(csv_files)} individual charts.")


if __name__ == "__main__":
    main()