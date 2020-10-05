# AI-Topics

This android app was made as a practice to topics learned in Harvard's CS50 - Introduction to AI course. The app includes multiple mini apps for topics like:  Search, Knowledge, Optimization, Reinforcement Learning and Natural Language Processing.

## Path Finder

A mini-app implementing breadth-first search algorithm to find the shortest path between a start point and an end point, while avoiding walls.The user can customize the maze by dragging and dropping walls, start block, and end block. 

### Operation

After the user places the blocks and starts the path finding algorithm, the application starts finding the shortest path, while displaying progress, i.e., painting the blocks visited by the algorithm yellow.
<p align="center"><img src="/screenshots/pathfinder_initial.jpeg" alt="Initial Maze" width="250" height="400">
&nbsp;&nbsp;&nbsp;
<img src="/screenshots/pathfinder_inprogress.jpeg" alt="Algorithm in Progress" width="250" height="400"></p>

After the algorithm finishes execution, i.e. finds the path to the end point, the shortest path between the start point and end point is painted green.
<p align="center"><img src="/screenshots/pathfinder_done.jpeg" alt="Shortest Path In Green" width="250" height="400"></p>


## Assisted Minesweeper

Using knowledge gathered while playing each step, this mini-app finds a safe block for the user whenever the user is stuck by refering to the knowledge base and avoiding mines.

### Operation

Every time the user clicks "Assist me" button, the app tries to find and reveal a safe cell.
<p align="center"><img src="/screenshots/minesweeper.jpeg" alt="Minesweeper" width="250" height="400"></p>
Initially, the knowledge base is not enough to infer which cells are safe, so the algorithm chooses cells randomly. However, as the knowledge base grows, the algorithm is able to accurately choose safe cells and avoid mines. <br></br>
Eventually, the user, with the help of AI, should be able to find all safe cells and win the game.
<p align="center"><img src="/screenshots/minesweeper_won.jpeg" alt="All Safe Cells Found" width="250" height="400"></p>


## Sudoku

Using backtracking algorithm learnt in "Optimization" topic in the course, this mini-app generates a 17-clue sudoku.
<p align="center"><img src="/screenshots/sudoku.jpeg" alt="17-clue Sudoku" width="250" height="400"></p>


## Tic-Tac-Toe

Using reinforcement learning, this mini-app is trained by playing against itself for thousands of times to be able to play against real users and make correct decisions based on its experience.
<p align="center"><img src="/screenshots/tictactoe.jpeg" alt="AI Beating User" width="250" height="400"></p>


## Arsenal Bot

A mini-app that uses natural language processing to answer user's query (regarding arsenal football team) based on information provided to the algorithm through text files. The app also converts the answer to speech for better user experience.
<p align="center"><img src="/screenshots/bot.jpeg" alt="Answering User Query" width="250" height="400"></p>


