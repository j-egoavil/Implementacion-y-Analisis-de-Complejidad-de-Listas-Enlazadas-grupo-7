import pandas as pd
import matplotlib.pyplot as plt
import os

DATA_DIR = "../data"
OUTPUT_DIR = "../docs/graficas"

os.makedirs(OUTPUT_DIR, exist_ok=True)

stack = pd.read_csv(os.path.join(DATA_DIR, "stack_push.csv"))
queue = pd.read_csv(os.path.join(DATA_DIR, "queue_enqueue.csv"))
lista = pd.read_csv(os.path.join(DATA_DIR, "list_pushfront.csv"))

plt.figure()

plt.plot(stack["size"], stack["time"], marker="o", label="Stack Push")
plt.plot(queue["size"], queue["time"], marker="o", label="Queue Enqueue")
plt.plot(lista["size"], lista["time"], marker="o", label="List PushFront")

plt.xlabel("Input Size")
plt.ylabel("Execution Time (ns)")
plt.title("Comparación de estructuras")

plt.xscale("log")

plt.legend()

output_path = os.path.join(OUTPUT_DIR, "comparacion_estructuras.png")

plt.savefig(output_path)

print("Graph saved:", output_path)

plt.show()