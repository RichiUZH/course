# Lab Journal: Exercise 3

*einträge mit Stern sind nicht zur Abgabe gedacht, sondern als Gedankenstütze während der Bearbeitung. 

Authors: Merian, Sybilla; Mühlemann, Mark; Stoher, Robin

##1. Velocity
In a first step, we will experiment with the money supply. First, we shall try to introduce a subsidy as a one-time event, distributed evenly on to all agents. In accordance with the Fisher equation, this should, in the long term and assuming 0% interest as well as constant velocity, lead to an equally large increase on the right hand side of the equation; the total (nominal) amount of transactions.

Following a table that shows the two different buffers and how they increase. The good shown is man hours. A buffer size of 1 is impossible because no money would flow. As we can see as the buffer size grows money gets scarce and therefore prices decrease because of a heavy deflation.

The simulation was run multiple times and the end volume and price was compared of each simulation.

![Table of different buffer sizes](/images/Screenshot_202017-10-12_2017.57.15.png "Table of different buffer sizes")


*Einfluss Money supply (gleichverteilt) festhalten. 

Since $V$ and $T$ ought to be constant and $M$ increases, the price level will have 

##2. Interest Rates
Following a table that shows the interest rates and how the two products man hour and potatoe and their price and volume act.
Interestingly a cap is reached in both products fast but once it is in prie and the other one in volume, while the other feature oscilates.
Manhour
|Interest		|Volume	   |Price   |
| ------------- |:-------------:| -----:|
|0.001		|381.98	|3.56|
|0.011		|377.88	|72.17|
|0.021		|378.05	|1332.72|
|0.031		|185.24	|3000.52|
|0.041		|160		|9015.72|
|0.051		|160		|5598.77|
|0.061		|160		|4355.33|
|0.071		|160		|6831.33|
|0.081		|160		|11821.15|
|0.091		|160		|11194.83|
|0.101		|160		|5602.16|

Potatoe
Interest	|Volume	|Price
| ------------- |:-------------:| -----:|
|0.001	|238.72	|5.75
|0.011	|234.39	116.64
|0.021	|	235.55|2204.57
|0.031	|	106.57|96676.79
|0.041	|	94.73|99019.8
|0.051	|	99.28|99019.8
|0.061	|	95.76|99019.8
|0.071	|	100.1|99019.8
|0.081	|	81.12|99019.8
|0.091	|	85.45|99019.8
|0.101	|	99.28|99019.8

*Falls die Wirtschaft sich bei einer Geldmengenerhöhung nicht wie erwartet verhält, liegt der Grund vermutlich darin, dass, wie im WhatsApp Chat erwähnt, es keine Geldmultiplikatoren wie Geschäftsbanken o.ä. gibt. Diese können mehr Kredite vergeben als eigentlich Geld in der Wirtschaft zur Verfügung steht. Dadurch weitet sich die Geldmenge um 1/delta Mindesteinlagensatz aus.

##3. Lump Sum Subsidies
By adding a fixed amount of money to the agents' account every day, he should, in theory, be willing to spend more (in absolute terms). This in turn would have to lead to other agents selling more goods, thus providing them with a higher utility. There are possibly some counteracting effects, such as certain agents disappearing from the market, because prices over time might rise, since in aggregate there is now more money in the economy. Additionaly, the agent will still maximize his utility and buy goods at the lowest price. This would favor efficient firms, and eliminate weaker firms that may not be able to compete in a price war. This of course is assuming that not all firms are producing at cost (yet).

Interestingly the more amount of one person does not change the live of the others that much compared with the previous experiments.
The average utility is more or less untouched, but somewhere between 950-20000 of the lump sum distribution the utility decreases harshly to a level of ~4.1.
As expected the lumps sum distribution creates an inflation, therefore the prices are getting higher but also the wages. Bigger effects are visible after 20000 lump sum where both volumes are reduced but no convergence can be spotted in this area.

Potatoe
LumpSum 	Volume	Price	Avg. Utility
|0		240.03	4.21		Avg: 4.499272436171293
|1		238.91	4.61		Avg: 4.496496000903647
2		233.87	5.16		Avg: 4.498141169084105
3		232.25	5.58		Avg: 4.481351386194255
4		240.15	5.82		Avg: 4.495751148018211
5		239.14	6.25		Avg: 4.496369579241927
6		238.36	6.85		Avg: 4.489918610831808
7		239.29	7.69		Avg: 4.4928775120722335
8		239.86	7.76		Avg: 4.492529322478359
9		239.34	7.81		Avg: 4.498206953446897
100		236.54	49.07	Avg: 4.495299266473854
110		238.79	54.48	Avg: 4.49272501228924
120		237.08	52.64	Avg: 4.494906975975532
130		240.98	64.75	Avg: 4.499617137714798
140		239.32	63.1		Avg: 4.494465914023529
750		238.58	331.41	Avg: 4.493323237978253
800		236.62	325.79	Avg: 4.4917857572105175
850		235.48	348.77	Avg: 4.492235705370925
900		240.04	375.12	Avg: 4.4942666017049175
950		239.5	419.76	Avg: 4.495862702193175
20000	170.26	12134.49	Avg: 3.9712120278012173
21000	170.93	12652.77	Avg: 4.0448286814891485
22000	151.54	15033.41	Avg: 4.194840740956112
23000	146.24	16213.56	Avg: 4.188088719570624
Man hour
LumpSum 	Volume	Price	Avg. Utility
|0		383.14	2.62		Avg: 4.499272436171293
1		382.02	2.86		Avg: 4.496496000903647
2		372.78	3.06		Avg: 4.498141169084105
3		376.97	3.32		Avg: 4.481351386194255
4		384.13	3.64		Avg: 4.495751148018211
5		382.99	3.88		Avg: 4.496369579241927
6		382.87	4.25		Avg: 4.489918610831808
7		384.56	4.8		Avg: 4.4928775120722335
8		385.63	4.87		Avg: 4.492529322478359
9		382.51	4.85		Avg: 4.498206953446897
100		379.26	29.87	Avg: 4.495299266473854
110		382.68	33.78	Avg: 4.49272501228924
120		380.21	32.2		Avg: 4.494906975975532
130		383.18	40.57	Avg: 4.499617137714798
140		383.77	39.32	Avg: 4.494465914023529
750		383.35	205.66	Avg: 4.493323237978253
800		380.35	199		Avg: 4.4917857572105175
850		378.24	210.83	Avg: 4.492235705370925
900		384.94	235.23	Avg: 4.4942666017049175
950		382.94	261.24	Avg: 4.495862702193175
20000	284.18	1688.08	Avg: 3.9712120278012173
21000	285.08	2017.21	Avg: 4.0448286814891485
22000	247.43	2945.91	Avg: 4.194840740956112
23000	243.71	3270.18	Avg: 4.188088719570624
*einkommens- und substitutionseffekt einbauen
