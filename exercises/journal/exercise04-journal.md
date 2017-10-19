# Lab Journal: Exercise 4

Authors: Mühlemann Mark, Merian Sybilla

## Task 1: Savings Rule

>### PAYGO vs Funded System
>As the given task dictates, we assume that the modeled economy can not rely on a PAYGO system (pay as you go, or _Umlaufverfahren_ in German). Instead, the agents use a funded system (_Kapitaldeckungsverfahren_). In this system, every agent saves a portion of his income; sets it aside while subjecting the money to interest (for example in a savings account or, ideally, an account with interest rates higher than inflation, such as a pension fund); and finances his existence in retirement with the resulting amount. Due to the effect of accumulated interest, the contributions set aside early in an agent's life, are responsible for the largest shares (relative to their initial size) of the capital dedicated to the period of retirement. **This could be taken in account by our agent, as he tries to optimise his lifetime utility.** As long as interest rates are higher than the population growth rate, the funded system performs better (measured by lifetime income) than a PAYGO system. To smoothen this effect (as well as for other reasons, such as redistributing wealth, most economies rely on a combination of both systems. 
*Dieser Paragraph muss noch an eine sinnvolle Stelle gesetzt werden und evtl weiter bearbeitet werden*

Agents are prohibited from working during their retirement. As shown (_WO? ERKLÄRUNG / BEWEIS CONSUMPTION SMOOTHING SYBILLA_),  they should aim to save a portion of the fruits of their labor, dedicated to consumption during this last period of their life. In accordance with the No-Ponzi condition, the capital of every agent must be non-negative at the end of his life cycle. There is no inheritance system in place and the agent derives no utility from owning money at the point of his death (as maybe would be the case in real life, leaving one's assets to an institution, such as a charity or school). Therefore, this share (combined with any interest it accumulates during his time as a worker and over the remaining 100 days of his life) should be consumed completely before the agent's demise. 

--

The agent maximizes his lifetime utility by consuming potatoes. While consuming a budget constraint has to be taken into account. The budget constraint contains that the money spent on lifetime consumption must equal the lifetime income. In our case the lifetime income ist the income from working and in case of firm owners the additional dividends. 
period budget constraint: 
$x_{t+s} * p_{t+s}= s_{t+s} + w_{t+s} - s_{t+s+1}$
As the agent does not invest its money but rather stores it under his pillow, we do not have to account for discounting. 
The agent than optimizes:
$max (\sum_{s=0}^{500}u(x_p,t))$ subjected to $x_{t+s} * p_{t+s}= s_{t+s} + w_{t+s} - s_{t+s+1}$
By rearaging we get: $max (\sum_{s=0}^{500}u({s_{t+s} + w_{t+s} - s_{t+s+1}}/p_{t+s}))$

In order to optimize the savings for the following day we get:
max $ u({s_{t+s} + w_{t+s} - s_{t+s+1}}/p_{t+s}) + u({s_{t+s+1} + w_{t+s+1} - s_{t+s+2}}/p_{t+s+1})

${\partial[.]}/{\partial {s_{t+s+1}}} = u'({s_{t+s} + w_{t+s} - s_{t+s+1}}/p_{t+s}) * {-1}/{p_{t+s}} + u'(s_{t+s+1} + w_{t+s+1} - s_{t+s+2}}/p_{t+s+1})*1/{p_{t+s+1}} =0$
$u'({s_{t+s} + w_{t+s} - s_{t+s+1}}/p_{t+s}) * 1/{p_{t+s}}= u'(s_{t+s+1} + w_{t+s+1} - s_{t+s+2}}/p_{t+s+1})*1/{p_{t+s+1}}$
\leftrightarrow $u'(x_{p,t+s}) * 1/{p_{t+s}}= u'(x_{p,t+s+1})*1/{p_{t+s+1}}$
Hence we get:
$u'(x_{p,t+s+1})/u'(x_{p,t+s})=p_{t+s+1}/p_{t+s}$
Therefore we get that the agent's consumption should change in order with the price change. Given prices are constant and we assume the log-utility, we get a consumption smoothing behavior. 



