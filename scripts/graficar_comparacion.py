from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd
from pandas.errors import EmptyDataError


SCRIPT_DIR = Path(__file__).resolve().parent
DATA_DIR = SCRIPT_DIR.parent / "data"
OUTPUT_DIR = SCRIPT_DIR.parent / "docs" / "graficas" / "comparaciones"
LIST_COMPARE_DIR = OUTPUT_DIR / "list-compare"
LIST_COMPARE_SINGLY_DIR = LIST_COMPARE_DIR / "singly"
LIST_COMPARE_DOUBLY_DIR = LIST_COMPARE_DIR / "doubly"
STACK_QUEUE_DIR = OUTPUT_DIR / "stack-vs-queue"


def y_column(df: pd.DataFrame) -> str:
    if "median_ns" in df.columns:
        return "median_ns"
    if "avg_time_ns" in df.columns:
        return "avg_time_ns"
    return "time"


def y_label(df: pd.DataFrame) -> str:
    if "median_ns" in df.columns:
        return "Tiempo (mediana, ns)"
    if "avg_time_ns" in df.columns:
        return "Tiempo (promedio, ns)"
    return "Tiempo (ns)"


def read_csv_if_exists(path: Path) -> pd.DataFrame | None:
    if not path.exists() or path.stat().st_size == 0:
        return None
    try:
        return pd.read_csv(path)
    except EmptyDataError:
        return None


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
        "push_vs_enqueue": "push vs enqueue",
        "pop_vs_dequeue": "pop vs dequeue",
        "peek_vs_front": "peek vs front",
        "delete_vs_delete": "delete vs delete",
    }
    return mapping.get(op, op)


def plot_series(
        series_list: list[tuple[str, Path]],
        title: str,
        output_path: Path,
        *,
        log_x: bool = True,
        log_y: bool = True,
) -> None:
    plt.figure(figsize=(10, 5.6))
    plotted = 0
    selected_y_label = "Tiempo (ns)"

    for label, csv_path in series_list:
        df = read_csv_if_exists(csv_path)
        if df is None or "size" not in df.columns:
            continue

        y_col = y_column(df)
        selected_y_label = y_label(df)

        clean_df = df[["size", y_col]].dropna()
        if clean_df.empty:
            continue

        plt.plot(clean_df["size"], clean_df[y_col], marker="o", linewidth=1.8, markersize=4.5, label=label)
        plotted += 1

    if plotted == 0:
        print(f"Skip {output_path.name}: no available data")
        plt.close()
        return

    plt.xlabel("Tamaño de entrada (n)")
    plt.ylabel(selected_y_label)
    plt.title(title)

    if log_x:
        plt.xscale("log")
    if log_y:
        plt.yscale("log")

    plt.grid(alpha=0.3, which="both")
    plt.legend()
    plt.tight_layout()

    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(output_path, dpi=180, bbox_inches="tight")
    plt.close()
    print(f"Saved: {output_path}")


def list_impl_csv_path(impl: str, op: str) -> Path:
    base = DATA_DIR / "data-list"
    mapping = {
        "singly": base / "list-singly" / "no-tail" / f"list_singly_{op}.csv",
        "singly_tail": base / "list-singly" / "whit-tail" / f"list_singly_tail_{op}.csv",
        "doubly": base / "list-doubly" / "no-tail" / f"list_doubly_{op}.csv",
        "doubly_tail": base / "list-doubly" / "whit-tail" / f"list_doubly_tail_{op}.csv",
    }
    return mapping[impl]


def clear_existing_pngs() -> None:
    for folder in [LIST_COMPARE_DIR, STACK_QUEUE_DIR]:
        if not folder.exists():
            continue
        for png in folder.rglob("*.png"):
            png.unlink()


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    LIST_COMPARE_DIR.mkdir(parents=True, exist_ok=True)
    LIST_COMPARE_SINGLY_DIR.mkdir(parents=True, exist_ok=True)
    LIST_COMPARE_DOUBLY_DIR.mkdir(parents=True, exist_ok=True)
    STACK_QUEUE_DIR.mkdir(parents=True, exist_ok=True)
    clear_existing_pngs()

    # Comparaciones de listas por operación entre implementaciones
    list_ops = [
        "push_front",
        "push_back",
        "pop_front",
        "pop_back",
        "find",
        "erase",
        "add_before",
        "add_after",
    ]

    for op in list_ops:
        singly_series = [
            ("Singly sin tail", list_impl_csv_path("singly", op)),
            ("Singly con tail", list_impl_csv_path("singly_tail", op)),
        ]
        doubly_series = [
            ("Doubly sin tail", list_impl_csv_path("doubly", op)),
            ("Doubly con tail", list_impl_csv_path("doubly_tail", op)),
        ]

        plot_series(
            singly_series,
            f"Comparación de listas singly: {pretty_op_name(op)}",
            LIST_COMPARE_SINGLY_DIR / f"list_compare_singly_{op}.png",
            )
        plot_series(
            doubly_series,
            f"Comparación de listas doubly: {pretty_op_name(op)}",
            LIST_COMPARE_DOUBLY_DIR / f"list_compare_doubly_{op}.png",
            )

    # Comparaciones stack vs queue
    sq_base = DATA_DIR
    equivalent_ops = [
        (
            "push_vs_enqueue",
            [
                ("Stack push", sq_base / "data-stack" / "stack_push.csv"),
                ("Queue enqueue", sq_base / "data-queue" / "queue_enqueue.csv"),
            ],
        ),
        (
            "pop_vs_dequeue",
            [
                ("Stack pop", sq_base / "data-stack" / "stack_pop.csv"),
                ("Queue dequeue", sq_base / "data-queue" / "queue_dequeue.csv"),
            ],
        ),
        (
            "peek_vs_front",
            [
                ("Stack peek", sq_base / "data-stack" / "stack_peek.csv"),
                ("Queue front", sq_base / "data-queue" / "queue_front.csv"),
            ],
        ),
        (
            "delete_vs_delete",
            [
                ("Stack delete", sq_base / "data-stack" / "stack_delete.csv"),
                ("Queue delete", sq_base / "data-queue" / "queue_delete.csv"),
            ],
        ),
    ]

    for name, series in equivalent_ops:
        plot_series(
            series,
            f"Stack vs Queue: {pretty_op_name(name)}",
            STACK_QUEUE_DIR / f"stack_queue_{name}.png",
            )

    # Cross compare principal: operación equivalente entre todas las estructuras
    cross_series = [
        ("Stack pop", DATA_DIR / "data-stack" / "stack_pop.csv"),
        ("Queue dequeue", DATA_DIR / "data-queue" / "queue_dequeue.csv"),
        ("List Singly pop_front", list_impl_csv_path("singly", "pop_front")),
        ("List Singly tail pop_front", list_impl_csv_path("singly_tail", "pop_front")),
        ("List Doubly pop_front", list_impl_csv_path("doubly", "pop_front")),
        ("List Doubly tail pop_front", list_impl_csv_path("doubly_tail", "pop_front")),
    ]
    plot_series(
        cross_series,
        "Cross compare: pop / dequeue / pop_front",
        STACK_QUEUE_DIR / "cross_pop_compare.png",
        )

    # Cross compare secundario opcional: consulta del elemento frontal/superior
    cross_peek_series = [
        ("Stack peek", DATA_DIR / "data-stack" / "stack_peek.csv"),
        ("Queue front", DATA_DIR / "data-queue" / "queue_front.csv"),
    ]
    plot_series(
        cross_peek_series,
        "Cross compare: peek / front",
        STACK_QUEUE_DIR / "cross_peek_front_compare.png",
        )


if __name__ == "__main__":
    main()