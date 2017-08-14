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
    # Z = si.griddata(list(zip(x.values,y.values)), z, (X,Y), method='linear')
    print(z.values)
    Z = np.array(z.values).reshape(len(x.unique()), len(y.unique()))

    surf = ax.plot_wireframe(X, Y, Z)

def main(args):

    engine = create_engine("mysql+pymysql://root:password@localhost/revgen")

    sql = """
    select 	theta_i0,
            theta_i1,
            if(count(d.sex_implant) = 0, 0, avg(d.sex_implant = 3)) as sex_to_implant,
            if(count(d.sex_implant) = 0, 0, avg(d.sex_implant = 3)) as implant_to_sex
    from
        ( 	select 	graph_results.*,
                    parameter_sets.*
            from	graph_results
                inner join simulations on graph_results.simulation_id=simulations.id
                inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
            where parameter_sets.run_id = {run_id}
                and sex_implant > 0
                and sex_ttr > 0
                and implant_ttr > 0
        ) as d
    group by id;
    """.format(run_id = args[1])

    sql2 = """
    select 	theta_i0,
            theta_i1,
            avg(d.sex_ttr = 2) as sex_to_ttr,
            avg(d.sex_ttr = 3) as ttr_to_sex
    from
        ( 	select 	graph_results.*,
                    parameter_sets.*
            from	graph_results
                inner join simulations on graph_results.simulation_id=simulations.id
                inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
            where parameter_sets.run_id = {run_id}
                and sex_implant > 0
                and sex_ttr > 0
                and implant_ttr > 0
        ) as d
    group by id;
    """.format(run_id = args[1])

    sql3 = """
    select 	theta_i0,
            theta_i1,
            avg(d.sex_ttr = 2) as implant_to_ttr,
            avg(d.sex_ttr = 3) as ttr_to_implant
    from
        ( 	select 	graph_results.*,
                    parameter_sets.*
            from	graph_results
                inner join simulations on graph_results.simulation_id=simulations.id
                inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
            where parameter_sets.run_id = {run_id}
                and sex_implant > 0
                and sex_ttr > 0
                and implant_ttr > 0
        ) as d
    group by id;
    """.format(run_id = args[1])


    df = pd.read_sql_query(sql, engine)
    df2 = pd.read_sql_query(sql2, engine)
    df3 = pd.read_sql_query(sql3, engine)

    fig1 = plt.figure()
    ax1 = fig1.add_subplot(231, projection='3d')
    ax4 = fig1.add_subplot(234, projection='3d')
    plot3d(df["theta_i0"], df["theta_i1"], df["sex_to_implant"], ax1)
    plot3d(df["theta_i0"], df["theta_i1"], df["implant_to_sex"], ax4)
    ax1.set_title("Sex -> Implant")
    ax4.set_title("Implant -> Sex")

    ax2 = fig1.add_subplot(232, projection='3d')
    ax5 = fig1.add_subplot(235, projection='3d')
    plot3d(df2["theta_i0"], df2["theta_i1"], df2["sex_to_ttr"], ax2)
    plot3d(df2["theta_i0"], df2["theta_i1"], df2["ttr_to_sex"], ax5)
    ax2.set_title("Sex -> TTR")
    ax5.set_title("TTR -> Sex")

    ax3 = fig1.add_subplot(233, projection='3d')
    ax6 = fig1.add_subplot(236, projection='3d')
    plot3d(df3["theta_i0"], df3["theta_i1"], df3["implant_to_ttr"], ax3)
    plot3d(df3["theta_i0"], df3["theta_i1"], df3["ttr_to_implant"], ax6)
    ax3.set_title("Implant -> TTR")
    ax6.set_title("TTR -> Implant")


    plt.show(1)




if __name__ == "__main__":
    main(sys.argv)

