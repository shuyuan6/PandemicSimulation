# PandemicSimulation
This project uses Java FX to visually simulate a pandemic model and plot curves that shows the occurrences of new cases and overall deaths. It verifies a well known theory - with no containment to an acute epidemic, the number of infections would grow exponential at the beginning. Then, as more people get infected, and then either die or become immune, we would reach a "turning point". The number of occurrence would drop significantly. Also, the shape of the curves would be affected by key parameters, such as disease transimitting probability, death rate after being affected, infection-to-death time, infection-to-recover time, etc. Having a medical background, I think it is particularly interesting to build software that applies to a real life world.

In the figures shown below, the leftside sub-figure shows a board of dots. A green dot represents a healthy individual and a red dot represents an infected inidividual. Then dots move with random initial speed and random initial direction. When a dot hits the boundary, it gets bounced back. When a healthy dot comes close enough to an infected dot, there is a probablity that the healthy dot get infected. The infected dot will either die after a period of time, or recover and become immnune after a period of time. The rightside sub-figure plots the number of newly infected dots and overall dead dots in real time.

## Start stage
This image shows that the count of the patients increased rapidly at the very early stage, the epidemic curve is like exponential growth.

![Image1 of PandemicSimulation](https://github.com/shuyuan6/PandemicSimulation/blob/master/Screen%20Shot%202020-09-28%20at%2012.44.10%20AM.png)

## Developing stage
As the time goes by, the epidemic curve reaches the peak, and go in into a short plateau period, the count of deaths emerges and increases.

![Image2 of PandemicSimulation](https://github.com/shuyuan6/PandemicSimulation/blob/master/Screen%20Shot%202020-09-28%20at%2012.44.23%20AM.png)

## Peak stage
As time progresses, most people have either got infected or dead. The number of new occurrences begins to drop significantly, while the overall death reaches record high.

![Image3 of PandemicSimulation](https://github.com/shuyuan6/PandemicSimulation/blob/master/Screen%20Shot%202020-09-28%20at%2012.44.33%20AM.png)

The new cases continue to drop and death toll continues to increase.

![Image4 of PandemicSimulation](https://github.com/shuyuan6/PandemicSimulation/blob/master/Screen%20Shot%202020-09-28%20at%2012.44.44%20AM.png)

## Final stage

Finally, the pandemic is over. We can see that a heavy price is paid. Only a small percentage of people survived.

![Image5 of PandemicSimulation](https://github.com/shuyuan6/PandemicSimulation/blob/master/Screen%20Shot%202020-09-28%20at%2012.44.56%20AM.png)


# Take-aways
Aside from all the technical stuffs that made the project work, it reinforced my belief in the importance of disease containment and immunization. Hats off to all the medical workers who are fighting to save people's lives.
