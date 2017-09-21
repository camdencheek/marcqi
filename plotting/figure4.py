#!/usr/bin/env python3

from sqlalchemy import create_engine
import sys
import pandas as pd
import scipy.stats
import numpy as np
import math
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import scipy.interpolate as si

def plot_3d(ax, run_id, engine):

    sql = """
        select
            theta_i0,
            theta_i1,
            avg(sex_implant > 0) as sex_implant,
            avg(sex_ttr > 0) as sex_ttr,
            avg(implant_ttr > 0) as implant_ttr
        from
            ( 	select
                    graph_results.*,
                    parameter_sets.*
                from	graph_results
                    inner join simulations on graph_results.simulation_id=simulations.id
                    inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
                where parameter_sets.run_id = {}
            ) as d
        group by id;
    """.format(run_id)

    df = pd.read_sql_query(sql, engine)

    x = df["theta_i0"]
    y = df["theta_i1"]
    z = df["implant_ttr"]

    test_x = np.linspace(0,0.3,100)
    test_y = np.linspace(0,0.3,100)
    X, Y = np.meshgrid(x.unique(), y.unique())
    Z = si.griddata(list(zip(x.values, y.values)), z, (X,Y), method='linear')

    surf = ax.plot_wireframe(X,Y,Z, linewidth=1)

    ax.set_xticklabels(ax.get_xticks() + .1, rotation=-35, ha='left', va='top')
    ax.set_yticklabels(ax.get_yticks() + .1, rotation=35, ha='right', va='top')
    ax.tick_params(axis='x', which='major', pad=-5)
    ax.tick_params(axis='y', which='major', pad=-5)
    ax.set_xlabel(r'$p_{I,F}$', fontsize='large')
    ax.set_ylabel(r'$p_{I,M}$', fontsize='large')
    ax.zaxis.set_rotate_label(False)
    ax.set_zlabel(r'Proportion with implant-ttr edge', rotation=90)
    ax.set_zlim(0.0, 1.0)
    ax.view_init(elev=20, azim=230)


def main(args):
    engine = create_engine("mysql+pymysql://root:password@localhost/revgen")

    fig = plt.figure(figsize=(3,9))


    ax1= fig.add_subplot(3,1,1, projection='3d')
    ax2 = fig.add_subplot(3,1,2, projection='3d')
    ax3 = fig.add_subplot(3,1,3, projection='3d')

    plot_3d(ax1, 4, engine)
    plot_3d(ax2, 3, engine)
    plot_3d(ax3, 5, engine)


    ax1.annotate("(a)", xy=(0.1, 0.9), xycoords='axes fraction',
                size='large', ha='center', va='top')
    ax2.annotate("(b)", xy=(0.1, 0.9), xycoords='axes fraction',
                size='large', ha='center', va='top')
    ax3.annotate("(c)", xy=(0.1, 0.9), xycoords='axes fraction',
                size='large', ha='center', va='top')

    fig.tight_layout(rect=[0.05,0.05, 0.95,0.95])
    plt.subplots_adjust(wspace=0.2)


    plt.savefig("../figures/figure4", dpi=300)



if __name__ == '__main__':
    main(sys.argv)




