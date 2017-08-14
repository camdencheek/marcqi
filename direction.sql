select 	theta_i0, 
		theta_i1, 
		avg(sex_implant > 0) as sex_implant, 
		avg(sex_ttr > 0) as sex_ttr, 
		avg(implant_ttr > 0) as implant_ttr
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
		inner join simulations on graph_results.simulation_id=simulations.id
		inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 1
	) as d
group by id;

select 	theta_i0, 
		theta_i1, 
		avg(sex_implant = 2) as sex_to_implant,
		avg(sex_implant = 3) as implant_to_sex
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	graph_results
			inner join simulations on graph_results.simulation_id=simulations.id
			inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 1
			and sex_implant > 0
	) as d
group by id;

select 	theta_i0, 
		theta_i1, 
		sex_ttr,
		sex_implant,
		implant_ttr,
		count(*) as sex_to_ttr,
		count(*) as ttr_to_sex
from 
	( 	select 	graph_results.*,
				parameter_sets.*
		from	parameter_sets
			inner join simulations on graph_results.simulation_id=simulations.id
			inner join parameter_sets on simulations.parameter_set_id=parameter_sets.id
		where parameter_sets.run_id = 3
	) as d
group by id;


select 	graph_results.*,
		parameter_sets.*
from 	graph_results
	join simulations on graph_results.simulation_id=simulations.id
	join parameter_sets on simulations.parameter_set_id=parameter_sets.id
where parameter_sets.run_id = 3;
# Get the proportion of graphs with an arc between the two variables that have the given direction
SELECT 	theta_i0,
		theta_i1,
		(SELECT AVG(sex_ttr = 2) where sex_ttr > 0 group by parameter_sets.id ) as sex_to_ttr,
		(SELECT AVG(sex_ttr = 3) where sex_ttr > 0 group by parameter_sets.id ) as ttr_to_sex,
		(SELECT AVG(sex_implant = 2) where sex_implant > 0 group by parameter_sets.id ) as sex_to_implant,
		(SELECT AVG(sex_implant = 3) where sex_implant > 0 group by parameter_sets.id ) as implant_to_sex,
		(SELECT AVG(implant_ttr = 2) where implant_ttr > 0 group by parameter_sets.id ) as implant_to_ttr,
		(SELECT AVG(implant_ttr = 3) where implant_ttr > 0 group by parameter_sets.id ) as ttr_to_implant
FROM graph_results as joined
	inner join simulations on joined.simulation_id = simulations.id
	inner join parameter_sets on simulations.parameter_set_id = parameter_sets.id
	where parameter_sets.run_id = 1
group by parameter_sets.id;

# Get the proportion of graphs that have the given direction between two variables
# Get the proportion of graphs with an arc between the two variables that have the given direction
SELECT 	theta_i0,
		theta_i1,
		(SELECT AVG(sex_ttr = 2) from graph_results where sex_ttr > 0 group by parameter_sets.id ) as sex_to_ttr,
		(SELECT AVG(sex_ttr = 3) from graph_results where sex_ttr > 0 group by parameter_sets.id ) as ttr_to_sex,
		(SELECT AVG(sex_implant = 2) from graph_results where sex_implant > 0 group by parameter_sets.id ) as sex_to_implant,
		(SELECT AVG(sex_implant = 3) from graph_results where sex_implant > 0 group by parameter_sets.id ) as implant_to_sex,
		(SELECT AVG(implant_ttr = 2) from graph_results where implant_ttr > 0 group by parameter_sets.id ) as implant_to_ttr,
		(SELECT AVG(implant_ttr = 3) from graph_results where implant_ttr  0 group by parameter_sets.id ) as ttr_to_implant
FROM graph_results as joined
	inner join simulations on joined.simulation_id = simulations.id
	inner join parameter_sets on simulations.parameter_set_id = parameter_sets.id
	where parameter_sets.run_id = 1
group by parameter_sets.id;

select * from parameter_sets where run_id = 1;