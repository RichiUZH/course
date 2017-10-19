# Lab Journal: Exercise 4

Authors: Mühlemann Mark, Merian Sybilla

## Task 1: Savings Rule

Agents are prohibited from working during their retirement. As shown below, they should aim to save a portion of the fruits of their labor, dedicated to consumption during this last period of their life. In accordance with the No-Ponzi condition, the capital of every agent must be non-negative at the end of his life cycle. There is no inheritance system in place and the agent derives no utility from owning money at the point of his death (as maybe would be the case in real life, leaving one's assets to an institution, such as a charity or school). Therefore, this share (combined with any interest it accumulates during his time as a worker and over the remaining 100 days of his life) should be consumed completely before the agent's demise. 

The agent maximizes his lifetime utility, measured in his consumption of potatoes. While consuming, a budget constraint has to be taken into account. The budget constraint postulates that the money spent on lifetime consumption must equal the lifetime income. In our case the lifetime income is the income provided through work. Dividends are not taken into account yet.

Period budget constraint: 

$x_{p,t+s} \cdot p_{t+s}= s_{t+s} + w_{t+s} - s_{t+s+1}$

Since the agent does not invest his money, but rather stores it under his pillow, we do not have to account for discounting. 
The agent then optimizes:

$\max \sum_{s=0}^{500}u(x_p,t))$ s.t. $x_{t+s} \cdot p_{t+s}= s_{t+s} + w_{t+s} - s_{t+s+1}$

By rearanging we get:

$\max \sum_{s=0}^{500}u(\frac{s_{t+s} + w_{t+s} - s_{t+s+1}}{p_{t+s}})$

In order to optimize the savings for the following day we get:

$\max u(\frac{s_{t+s} + w_{t+s} - s_{t+s+1}}{p_{t+s}}) + u(\frac{s_{t+s+1} + w_{t+s+1} - s_{t+s+2}}{p_{t+s+1}})$

$\frac{\partial{[.]}}{\partial{s_{t+s+1}}} = u'(\frac{s_{t+s} + w_{t+s} - s_{t+s+1}}{p_{t+s}}) \cdot -\frac{1}{p_{t+s}} + u'(\frac{s_{t+s+1} + w_{t+s+1} - s_{t+s+2}}{p_{t+s+1}}) \cdot \frac{1}{p_{t+s+1}} =0$

$u'(\frac{s_{t+s} + w_{t+s} - s_{t+s+1}}{p_{t+s}}) \cdot \frac{1}{p_{t+s}} = u'(\frac{s_{t+s+1} + w_{t+s+1} - s_{t+s+2}}{p_{t+s+1}}) \cdot \frac{1}{p_{t+s+1}}$

$\Leftrightarrow u'(x_{p,t+s}) \cdot \frac{1}{p_{t+s}}= u'(x_{p,t+s+1}) \cdot \frac{1}{p_{t+s+1}}$

Hence we get: $\frac{u'(x_{p,t+s+1})}{u'(x_{p,t+s})}=\frac{p_{t+s+1}}{p_{t+s}}$

Therefore we get that the agent's consumption should change in order with the price change. 
Given logarithmic utility and that prices are constant during the first 400 days, we get a consumption smoothing behavior.

$u(x_{p,t})= log(h_l)+ log(x_{p,t})$ 

$u'(x_{p,t})= \frac{1}{x_{p,t}}$

$\frac{x_{p,t+s}}{x_{p,t+s+1}}=\frac{p_{t+s+1}}{p_{t+s}}$ 

Therefore we get the following saving heuristic in case of no dividends: If prices are increased by a factor $\alpha$, decrease consumption by the factor $\frac{1}{\alpha}$ and vice versa.

By assuming constant prices and wages, we can than calculate the optimal consumption of potatoes. If we introduce uncertainty about prices and dividends, the agents optimizes his utility based on his expectation conditional on his information set at the current time. Hence the agent has to form beliefs about the development of prices and dividends. 

## Task 2: Simulation
Because the agent does not have certainty over the development of potato prices,
>We assume that wages move perfectly with the price of potatoes, and  making prices stable relative to 
### Simple heuristic
In a first approach, we programmed our agent to follow a very simple spending pattern, starting on the first day of his retirement: 

```javascript
this.savings=money/(100-this.getAge()-400)*1.001;
```
or, in more relatable terms (at least for economists)

$\text{Daily spending allowance}=\text{Savings}-(\frac{\text{Savings}}{500-age} \cdot 1.001)$

The agent is advised to divide his current savings by his remaining days alive, add a margin of security of 0.1%, and setting this aside. He then is to spend the entire remaining amount on potatoes. 

### Calculating 

___
## Side note: What happens if interest is introduced?
In the following, we discuss some thoughts on the influence of interest on the problem at hand. 
### PAYGO vs. Funded System
So far we have assumed that the modeled economy can not rely on a PAYGO system (pay as you go, or _Umlaufverfahren_ in German). Instead, ceteris paribus, the agents would be using a funded system (_Kapitaldeckungsverfahren_). In this system, every agent saves a portion of his income and sets it aside. Instead of keeping his savings under the mattress, he would subject the money to interest (for example in a savings account or, ideally, an account with interest rates higher than inflation, such as a pension fund); and finance his existence in retirement with the resulting amount. As long as interest rates are higher than the population growth rate, the funded system performs better (measured by lifetime income) than a PAYGO system. To smoothen this effect (as well as for other reasons, such as redistributing wealth, most economies rely on a combination of both systems. 

### Consequences for agents
Due to the effect of accumulated interest, the contributions set aside early in an agent's life, are responsible for the largest shares (relative to their initial size) of the capital dedicated to the period of retirement. This could be taken in account by our agent as he tries to optimize his lifetime utility. However, due to the logarithmic utility function (constant relative risk aversion), he will still attempt to smooth his consumption in every period and over all periods of his life. 

### Introducing a PAYGO System
Enabling the agents to make use of a PAYGO system, would somewhat mitigate the effect of price fluctuation. A portion of the wages earned by the working population in the examined period would be made available to the retirees, thus reducing their need to periodically recalculalating their savings plan. Given a sufficient amount of trust in the system and it's continuity, working generations would be able to better maximize their lifetime utility. 
