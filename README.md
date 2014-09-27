# CEO (Confirm Ensured Organized)

Basic user manual

---

Basic Command Line Interface Usage
------

 1. Add a task:
 
 `add [-N or --title <title>] ([-D or --description <description>] [-L or --location <location>] [-T or --time {<date+time>| <<starting date+time> to <ending date+time>>}] [-R or --recurring <number h/d/w/m/y>])`
 
 Note: date+time format: YYYY/MM/DD/hh:mm
 
 2. Show tasklist:
 
 `list [--type <floating|deadline|periodic|all>] ([-S or --sort])`
 
 3. Show task detail:
 
 `show <task ID>`
 
 4. Update a task:
 
 `update <task ID> ([-N or --title <title>] [-C or --complete {true|false}] [-D or --description <description>] [-L or --location <location>] [-T or --time {<date+time>| <<starting date+time>,<ending date+time>>}] [-R or --recurring <number h/d/w/m/y>])`
 
 5. Delete a task:
 
 `Delete <task ID>`
 
 6. Search for a task:
 
 `search {[-N or --title <title keyword>]|[-C or --complete {true|false}]|[-D or --description <description keyword>]|[-L or --location <location>]|[-T or --time {<date+time>| <<starting date+time> to <ending date+time>>}]|[-R or --recurring <number h/d/w/m/y>]}`
 
 7. Search for an empty slot:

 `searchempty <<starting date+time> to <ending date+time>>`
 
 8. Undo last changes:
 
 `undo <steps>`
 
 9. Display Help:
 
 `help`

