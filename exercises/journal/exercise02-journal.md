# Lab Journal: Exercise 2

Authors: name surname, name surname

This is your lab journal. Use it to protocol what you did (even if it did not work) and to describe how you tried to solve the exercise. In case an experiment did not turn out as expected, describe what you did, what you expected to happen, and what actually happened.

This file is in markdown format and rendered nicely in the web. See [this link](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet) for further information about how markdown works.

Author: Merian Sybilla, Stohler Robin

Before modifing the code, we tried to calculate the optimal number of firms mathematically. Therefore we optimized the aggregate production function. As the firm takes the desicion of the consumers /farmers into account, we first had to calculate how many hours they want to work. The optimization of the conumers can be done on the individual or on a aggregate level. If the consumers beahve as if they were one big consumer, the production function should change due to the replication argument. We found that it does not matter whether we calcluate it on the aggregate level or on the individual level, as we optimize over the optimal hours of work. We got k*=19.45 and a total labor supply of 15 hours per day for each farm. 

By analyzing the simulations we found out that our dividends are more or less decreasing over time. In comparison with the other teams dividends we tried to let our dividends fluctuate more. We've done this by including different approaches like for example adding an extra dividend whenever there were three positive-profit-days in a row (like a bonus).Our intention was to attract more workers by providing such a extra payment. Overall we did not manage to get a much better result with this approach.  
Secondly we tried to change the budget in order to get a better result. We calculated the reserves as the sum of all profits minus the sum of all dividends. We had the idea that whenever the reserves go beyond a certain threshold we only use 80% of the budget in order to balance the budget. This had no big influence on our agent.
In the simulation we can see that our firm is on the market from the second day on. For the first 21 days there are only 2 frims on the market. Beginning day 28, or dividends start do decrease after reaching a peek. This could be explained by the entry of the other farms into the market. 

In this exercise mostly variable tuning was tried in order to get a high utility. Starting with the dividends first with an own approach then with the suggested, then man hour, budget. Unfortunately I could not see through the code quite yet. A cheat sheet of the methods and their output would be helpful. What is the difference between money and cash? what is th use of the dividends wallet etc.

