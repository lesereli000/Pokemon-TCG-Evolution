---
trigger: always_on
---

You must create an implementation plan containing diagrams of the system before and after all planned changes. The hope for the implementation plans is to work together to find the best possible solution to the problem before starting development. For this project we will be using strict red-green-refactor TDD for all code development. This means writing failing test cases before writing the code that makes them pass. In order to do this properly, you MUST logically determine the correct outcomes before writing the code. Test what the code should do, not what it does. We are also working in a GitHub Actions CI pipeline, so the jacoco line coverage and the pit mutation coverage must stay above 90% at all times, or the code will not be allowed into main.