import os
import pandas as pd
import matplotlib.pyplot as plt

DATA_DIR = "../data"
OUTPUT_DIR = "../docs/graficas"

os.makedirs(OUTPUT_DIR, exist_ok=True)

def plot_csv(file_name):

    file_path = os.path.join(DATA_DIR, file_name)

    data = pd.read_csv(file_path)

    plt.figure()

    plt.plot(data["size"], data["time"], marker="o")

    plt.xlabel("Input Size")
    plt.ylabel("Execution Time (ns)")
    plt.title(file_name.replace(".csv",""))

    plt.xscale("log")

    output = os.path.join(
        OUTPUT_DIR,
        file_name.replace(".csv",".png")
    )

    plt.savefig(output)

    print("Saved:", output)


for file in os.listdir(DATA_DIR):

    if file.endswith(".csv"):
        plot_csv(file)

plt.show()