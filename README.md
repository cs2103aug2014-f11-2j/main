# CEO (Confirm Ensured Organized)

Basic user manual

---

Basic Command Line Interface Usage
------

 1. Add a task:
 
 `add [-N or --title <title>] ([-D or -description <description>] [-L or -location <location>] [-T or -time {<blank>|<yyyy/MM/dd HH:mm>|<<yyyy/MM/dd HH:mm> to <yyyy/MM/dd HH:mm>>}] [-R or -recurring <number h/d/w/m/y>])`
 
 Note: date+time format: YYYY/MM/DD/hh:mm
 
 2. Show tasklist:
 
 `List <floating|deadline|periodic|all>`
 
 3. Show task detail:
 
 `show <task ID>`
 
 4. Update a task:
 
 `update <task ID> ([-N or -title <title>] [-C or -complete {true|false}] [-D or -description <description>] [-L or -location <location>]  [-T or -time {<blank>|<yyyy/MM/dd HH:mm>|<<yyyy/MM/dd HH:mm> to <yyyy/MM/dd HH:mm>>}] [-R or -recurring <number h/d/w/m/y>])`
 
 5. Delete a task:
 
 `Delete <task ID>`
 
 6. Search for tasks:
 
 `search (<floating|deadline|periodic|all>) {[-K or --keyword <title/description/location keyword>] [-C or --complete {true|false}] [-T or -time {<blank>|<yyyy/MM/dd HH:mm>|<<yyyy/MM/dd HH:mm> to <yyyy/MM/dd HH:mm>>}]}`
 
 7. Undo last changes:
 
 `undo <steps>`
 
 8. Redo last undos:
 
 `redo <steps>`
 
 9. Display Help:
 
 `help (<add|list|show|delete|update|undo|redo|search>)`

