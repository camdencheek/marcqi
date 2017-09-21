#!/usr/bin/python3

import pymysql.cursors
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
from sqlalchemy import create_engine
import pandas as pd
from matplotlib import cm
from matplotlib.ticker import MaxNLocator
import scipy.interpolate as si
import sys

def plot3d(x,y,z, ax):

    test_x = np.linspace(0,0.3,100)
    test_y = np.linspace(0,0.3,100)
    X,Y = np.meshgrid(x.unique(),y.unique())
    # X,Y = np.meshgrid(test_x,test_y)
    Z = si.griddata(list(zip(x.values,y.values)), z, (X,Y), method='linear')

    surf = ax.plot_wireframe(X, Y, Z)

def main(args):

    engine = create_engine("mysql+pymysql://root:rBep8emek!@192.168.1.56/revgen")

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
                where parameter_sets.run_id = {run_id}
            ) as d
        group by id;
    """.format(run_id = args[1])

    df = pd.read_sql_query(sql, engine)
    fig1 = plt.figure()
    ax1 = fig1.add_subplot(131, projection='3d')
    ax2 = fig1.add_subplot(132, projection='3d')
    ax3 = fig1.add_subplot(133, projection='3d')
    plot3d(df["theta_i0"], df["theta_i1"], df["sex_implant"], ax1)
    ax1.set_title("Sex -> Implant")
    ax1.set_xlabel("theta_I_F")
    ax1.set_ylabel("theta_I_M")
    ax1.set_zlim(0.0,1.0)
    plot3d(df["theta_i0"], df["theta_i1"], df["sex_ttr"], ax2)
    ax2.set_title("Sex -> TTR")
    ax2.set_xlabel("theta_I_F")
    ax2.set_ylabel("theta_I_M")
    ax2.set_zlim(0.0,1.0)
    plot3d(df["theta_i0"], df["theta_i1"], df["implant_ttr"], ax3)
    ax3.set_title("Implant -> TTR")
    ax3.set_xlabel("theta_I_F")
    ax3.set_ylabel("theta_I_M")
    ax3.set_zlim(0.0,1.0)
    plt.show(1)




if __name__ == "__main__":
    main(sys.argv)

