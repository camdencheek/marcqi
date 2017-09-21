#!/usr/bin/env python3

from sqlalchemy import create_engine
import sys
import pandas as pd
import scipy.stats
import numpy as np
import math
from ci import clopper_pearson
import matplotlib.pyplot as plt

def plot_2d(ax, run_id, engine):

    sql = """
    select
        sum(sex_implant > 0) as sex_implant,
        sum(sex_ttr > 0) as sex_ttr,
        sum(implant_ttr > 0) as implant_ttr,
        parameter_sets.beta00 / parameter_sets.beta01 as effect_size
    from graph_results
        inner join simulations on graph_results.simulation_id = simulations.id
        inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
    where parameter_sets.run_id = 18
    group by effect_size;
    """.format(run_id)

    df = pd.read_sql_query(sql, engine)

    x = df["effect_size"]
    y = df["implant_ttr"]
    n = 1000

    lb = [i / n - clopper_pearson(i, n, alpha = 0.05)[0] for i in y ]
    ub = [clopper_pearson(i, n, alpha = 0.05)[1] - i / n for i in y ]

    ax.errorbar(x, y/n, yerr=[lb,ub], capsize=3, linewidth=1)
    ax.set_ylim([0.0,1.1])


def main(args):
    engine = create_engine("mysql+pymysql://root:password@localhost/revgen")

    fig = plt.figure(figsize=(9,7))


    fig.text(0.5,0.02, "Effect size $E_I$",
             va='bottom', ha='center', size='x-large')
    fig.text(0.02,0.5, "Proportion with implant-ttr edge",
             va='center', ha='left', rotation=90, size='x-large')

    ax1= fig.add_subplot(2,2,1)
    ax2 = fig.add_subplot(2,2,2)
    ax3 = fig.add_subplot(2,2,3)
    ax4 = fig.add_subplot(2,2,4)

    plot_2d(ax1, 15, engine)
    plot_2d(ax2, 16, engine)
    plot_2d(ax3, 18, engine)
    plot_2d(ax4, 17, engine)


    ax1.annotate("(a)", xy=(0.1, 0.9), xycoords='axes fraction',
                size='large', ha='center', va='top')
    ax2.annotate("(b)", xy=(0.1, 0.9), xycoords='axes fraction',
                size='large', ha='center', va='top')
    ax3.annotate("(c)", xy=(0.1, 0.9), xycoords='axes fraction',
                size='large', ha='center', va='top')
    ax4.annotate("(d)", xy=(0.1, 0.9), xycoords='axes fraction',
                size='large', ha='center', va='top')

    fig.tight_layout(rect=[0.05,0.05, 0.95,0.95])

    plt.savefig("../figures/figure2", dpi=300)


if __name__ == '__main__':
    main(sys.argv)




