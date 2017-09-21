#!/usr/bin/python3

from sqlalchemy import create_engine
import sys
import pandas as pd
import scipy.stats
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
import numpy as np
import math
from ci import clopper_pearson


def plot_2d(x,y,n,filename):
    fig = plt.figure()
    ax = fig.add_subplot(111)
    lb = [i / n - clopper_pearson(i, n, alpha = 0.05)[0] for i in y ]
    ub = [clopper_pearson(i, n, alpha = 0.05)[1] - i / n for i in y ]

    ax.errorbar(x,y/n, yerr=[lb, ub],capsize=3)
    ax.set_ylim([0.0,1.0])
    ax.set_xlabel("$E_I$")
    ax.set_ylabel("Proportion with implant--ttr arc")
    plt.savefig(filename)
    plt.show()

def main(args):
    engine = create_engine("mysql+pymysql://root:password@localhost/revgen")

    sql = """
    select
        sum(sex_implant > 0) as sex_implant,
        sum(sex_ttr > 0) as sex_ttr,
        sum(implant_ttr > 0) as implant_ttr,
        parameter_sets.beta00 / parameter_sets.beta01 as effect_size
    from graph_results
        inner join simulations on graph_results.simulation_id = simulations.id
        inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
    where parameter_sets.run_id = {run_id}
    group by effect_size;
    """.format(run_id = args[1])

    df = pd.read_sql_query(sql, engine)

    plot_2d(df["effect_size"], df["implant_ttr"], 1000, args[2])

if __name__ == "__main__":
    main(sys.argv)

