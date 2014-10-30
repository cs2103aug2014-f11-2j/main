# CEO (Confirm Ensured Organized)

Basic user manual

---

Basic Command Line Interface Usage
------

 1. Add a task:
 
 `add <Quick add string> ([-N or --title <title>] [-D or -description <description>] [-L or -location <location>] [-T or -time {<blank>|<time>|<<time> to <time>>}] [-R or -recurring <number h/d/w/m/y>])`
 
 2. Show tasklist:
 
 `List (<floating|deadline|periodic|all|trash>)`
 
 3. Show task detail:
 
 `show <task ID>`
 
 4. Update a task:
 
 `update <task ID> ([-N or -title <title>] [-C or -complete {true|false}] [-D or -description <description>] [-L or -location <location>]  [-T or -time {<blank>|<time>|<<time> to <time>>}] [-R or -recurring <number h/d/w/m/y>])`
 
 5. Delete a task:
 
 `Delete <task ID> (-p)`
 
 6. Search for tasks:
 
 `search (<title/description/location keyword>) {[-K or -type <floating|deadline|periodic|all|trash>] [-C or -complete {true|false}] [-T or -time {<blank>|<time>|<<time> to <time>>}]}`
 
 7. Undo last changes:
 
 `undo <steps>`
 
 8. Redo last undos:
 
 `redo <steps>`
 
 9. Display Help:
 
 `help (<add|list|show|delete|update|undo|redo|search>)`
 
 10. Sync with Google:
 
 `sync (-disable)`
 
 11. Exit the program:
 
 `exit`

