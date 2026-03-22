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

    # Fallback for unexpected files, keep deterministic path.
    safe_name = "__".join(relative.with_suffix("").parts)
    return OUTPUT_DIR / f"{safe_name}.png"


def plot_csv(csv_path: Path) -> None:
    try:
        data = pd.read_csv(csv_path)
    except EmptyDataError:
        print(f"Skip empty CSV: {csv_path}")
        return

    if "size" not in data.columns:
        print(f"Skip (no size column): {csv_path}")
        return

    y_col = y_column(data)
    plt.figure(figsize=(8, 5))
    plt.plot(data["size"], data[y_col], marker="o")
    plt.xlabel("Input size")
    plt.ylabel("Time (ns)")
    plt.title(csv_path.stem)
    plt.xscale("log")
    plt.grid(alpha=0.3)
    plt.tight_layout()

    output_path = output_path_for_csv(csv_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=160)
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