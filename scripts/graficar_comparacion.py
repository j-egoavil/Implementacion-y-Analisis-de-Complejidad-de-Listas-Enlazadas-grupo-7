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
	if "avg_time_ns" in df.columns:
		return "avg_time_ns"
	return "time"


def read_csv_if_exists(path: Path) -> pd.DataFrame | None:
	if not path.exists() or path.stat().st_size == 0:
		return None
	try:
		return pd.read_csv(path)
	except EmptyDataError:
		return None


def plot_series(series_list: list[tuple[str, Path]], title: str, output_path: Path) -> None:
	plt.figure(figsize=(9, 5))
	plotted = 0

	for label, csv_path in series_list:
		df = read_csv_if_exists(csv_path)
		if df is None or "size" not in df.columns:
			continue
		y_col = y_column(df)
		plt.plot(df["size"], df[y_col], marker="o", label=label)
		plotted += 1

	if plotted == 0:
		print(f"Skip {output_path.name}: no available data")
		plt.close()
		return

	plt.xlabel("Input size")
	plt.ylabel("Time (ns)")
	plt.title(title)
	plt.xscale("log")
	plt.grid(alpha=0.3)
	plt.legend()
	plt.tight_layout()

	output_path.parent.mkdir(parents=True, exist_ok=True)
	plt.savefig(output_path, dpi=160)
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

	# List comparisons by operation across implementations.
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
			("List Singly no-tail", list_impl_csv_path("singly", op)),
			("List Singly tail", list_impl_csv_path("singly_tail", op)),
		]
		doubly_series = [
			("List Doubly no-tail", list_impl_csv_path("doubly", op)),
			("List Doubly tail", list_impl_csv_path("doubly_tail", op)),
		]
		plot_series(singly_series, f"List comparison (singly): {op}", LIST_COMPARE_SINGLY_DIR / f"list_compare_singly_{op}.png")
		plot_series(doubly_series, f"List comparison (doubly): {op}", LIST_COMPARE_DOUBLY_DIR / f"list_compare_doubly_{op}.png")

	# Stack vs queue equivalent operations.
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
		plot_series(series, f"Stack vs Queue: {name}", STACK_QUEUE_DIR / f"stack_queue_{name}.png")

	# Cross-structure reference chart requested in workshop.
	cross_series = [
		("List push_front (singly)", list_impl_csv_path("singly", "push_front")),
		("Stack push", DATA_DIR / "data-stack" / "stack_push.csv"),
		("Queue enqueue", DATA_DIR / "data-queue" / "queue_enqueue.csv"),
	]
	plot_series(cross_series, "List vs Stack vs Queue (insert)", STACK_QUEUE_DIR / "cross_insert_compare.png")


if __name__ == "__main__":
	main()