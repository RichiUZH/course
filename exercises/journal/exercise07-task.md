# Exercise 7 - Equality

One of the strength of agent-based models is their ability to allow for diversity. Instead of just having one representative consumer, we can have hundreds of individual consumers, that each have their own wealth, their own income, their own age, and even their own utility functions. This makes it straight-forward to measure inequality, as well as other metrics that are based on variation within the population.

In this exercise, you will test the effect of varying utility functions as well as varying life expectancy on equality, as measured by the [Gini coefficient](https://en.wikipedia.org/wiki/Gini_coefficient), which is the most common metrics to measure equality.

![equality](images/ex7-gini-world.png "Income inequality in various countries")

This exercise is based on the known simulation from the previous exercises. However, to reduce noise, the [demographics](http://meissereconomics.com/vis/simulation?sim=ex7-equality-basic&metric=demographics) are not random any more and the population kept constant after an initial growth phase. To further decrease noise, inheritance rules are adjusted, dividing all wealth of the dead equally among the whole population instead of assigning it to a random newborn as before. The [consumers](https://github.com/meisser/course/blob/ex7-equality-basic/simulation/src/com/agentecon/consumer/InvestingConsumer.java) are investing according to the optimal (corrected) investment rule, assuming a daily interest rate of 0.5%. They pick the stocks with the highest dividend yields, but disregard yields when selling them.

## Task 1

Looking at the [equality statistics](http://meissereconomics.com/vis/simulation?sim=ex7-equality-basic&metric=equality) of the basic scenario, you will notice that equality is much lower in the whole population than in each cohort individually! Overall, the wealth gini coefficient hovers above 0.4, but it rarely goes above 0.1 when looking at each cohort on its own. Can you explain why the observed inequality decreases when looking more "closely"?

## Task 2

So far, all agents have been born equal, just at different points in time. Let us check what happens when we give them varying utility functions. In particular, the simulation [ex7-equality-util-variation](http://meissereconomics.com/vis/simulation?sim=ex7-equality-util-variation&metric=equality) is configured to give each agent a randomized utility function

$U(x_p, h_l) = \alpha log x_p + \beta log h_l$ 

with $\alpha + \beta = 2$ and $0.5 < \alpha < 1.5$ being randomly chosen in that range, using the uniform probability distribution. 

Note that productivity has been adjusted such that the utility enjoyed from leisure time and the utility enjoyed from eating potatoes is about the same in the previous setting with $\alpha = \beta = 1$. So it is not so clear whether having a high $\alpha$ or a high $\beta$ is better.

Compare wealth and the utility equality of this new setting with the previous one. What do you observe?

## Task 3

Finally, you should find out what effect an increased life expectancy has on wealth equality. To do so, fetch the latest version of the simulation and run it with different values for the maxAge parameter in the [AgeConfiguration](../src/com/agentecon/exercise7/AgeConfiguration.java). Note that the version that is running on the server has set this parameter to 500 as before, so you need to adjust it on your own and run it locally.

What happens to inequality in our model population as life expectancy is increased? Why?

## Deliverables and deadline

Document your findings in the [lab journal](exercise07-journal.md), including an equation describing the relation between flows and prices, as well as the results of you statistic tests.

The deadline for submitting the lab journal to github is 2017-11-09 at 24:00.
