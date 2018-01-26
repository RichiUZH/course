# Lab Journal: Exercise 9

Authors: name surname, name surname

## Task

Feel free to comment on what you did. The grading will be based on your code and the presentation. Explanations for what you did should either be in the code (as comment), or in the presentation.

## Grading
by Luzius Meisser

Very nice presentation, I like the mountain logo.

You put a lot of effort into trying out different strategies and coming up with a good mixture of strategies. So overall, there is strong thinking, but a lack of luck with the execution.

However, you still got beaten by the other investment fund. Initially, we thought this was due to them buying your shares. But upon further investigation, there are other reasons for that. Buying your shares was just a symptom of being very successful overall, i.e. they bought shares of all companies. Their main two secret ingredients were retaining dividends and keeping a cash reserve to smoothen investments (the incoming dividends are quite volatile). In particular, the other fund keeps a cash reserve by only buying every 14 days. This is equivalent to only investing 1/14th of the normal amount every day. So unlike suspected in the discussion, this is not about the rhythm, but about keeping cash reserves.

Also, I found out that on line 351 in your code, in the P/E strategy, you calculate the price with *double share_price = Math.abs(.5*(ask - bid));* . This is wrong, there should be a + instead of a -. So one of the major components of your overall strategy actually did not work as intended. Furthermore, the comment describing the "Harunsche formel" (0.4*market-pf + 0.3*P/E + 0.2*DivYield + 0.05*Young + 0.05*P/B) is not up to date and does not reflect what the code actually does. If you invest 30% of the available cash reserves first and then another 30% of the remaining cash reserves, you only invested 51% of the initial reserves, and not 60%.

To summarize: I like how you applied knowledge from other courses and implemented according strategies. That's what interdisciplinary collaboration should be about! Also, I think you all understood the idea of what you were doing. However, you were somewhat unfortunate on the execution side with that bug in the main strategy. Also, it would have helped to keep a cash reserve and to be picky when selling shares (not only when buying them).

(to be updated later)
