# Get averages of whether the direction exists
select id, theta_i0, avg(sex_implant > 0), avg(sex_ttr > 0), avg(implant_ttr > 0)
from 
  (  select graph_results.*, 
      parameter_sets.* 
    from graph_results
    inner join simulations on graph_results.simulation_id=simulations.id
    inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id 
    where parameter_sets.run_id = 7
  ) as d
group by id;

# Sex is parent of implant
select id, theta_i0, avg(sex_implant = 2 )
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
		inner join simulations on graph_results.simulation_id=simulations.id
		inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 7
			and sex_implant > 0
	) as d
group by id;

# Sex is parent of implant
select id, theta_i0, avg(sex_implant = 2 )
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
		inner join simulations on graph_results.simulation_id=simulations.id
		inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 7
			and sex_implant > 0
	) as d
group by id;

# Implant is parent of sex
select id, theta_i0, avg(sex_implant = 3 )
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
		inner join simulations on graph_results.simulation_id=simulations.id
		inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 7
			and sex_implant > 0
	) as d
group by id;

# Sex is parent of TTR
select id, theta_i0, avg(sex_ttr = 2 )
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
		inner join simulations on graph_results.simulation_id=simulations.id
		inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 7
			and sex_ttr > 0
	) as d
group by id;
	
# TTR is parent of sex
select id, theta_i0, avg(sex_ttr = 3 )
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
		inner join simulations on graph_results.simulation_id=simulations.id
		inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 7
			and sex_ttr > 0
	) as d
group by id;
	
# Implant is parent of TTR
select id, theta_i0, avg(implant_ttr = 2 )
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
		inner join simulations on graph_results.simulation_id=simulations.id
		inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 7
			and implant_ttr > 0
	) as d
group by id;
	
# TTR is parent of implant
select id, theta_i0, avg(implant_ttr = 3 )
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
		inner join simulations on graph_results.simulation_id=simulations.id
		inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 7
			and implant_ttr > 0
	) as d
group by id;
