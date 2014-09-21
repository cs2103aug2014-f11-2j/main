# CEO (Confirm Ensured Organized)

Basic user manual

---

Basic Command Line Interface Usage
------

 1. Add a task:
 add [-N or --title <title>] ([-D or --description &lt;description>] [-L or --location &lt;location>] [-C or --category &lt;category name>] [-T or --time {&lt;date+time>| &lt;&lt;starting date+time> to &lt;ending date+time>>}] [-R or --recurring &lt;number h/d/w/m/y>] [-I or --importance &lt;importance level>)
Note: date+time format: YYYY/MM/DD/hh:mm
 2. Show tasklist:
 list [--type &lt;floating|deadline|periodic|all>] ([-S or --sort])
 3. Show task detail:
 show &lt;task ID>
 4. Update a task:
 update <task ID> ([-N or --title &lt;title>] [-P or --progress {n(incomplete)| p(in progress)| c(completed)}] [-D or --description &lt;description>] [-L or --location &lt;location>] [-C or --category &lt;category name>] [-T or --time {&lt;date+time>| &lt;&lt;starting date+time>,&lt;ending date+time>>}] [-R or --recurring &lt;number h/d/w/m/y>] [-I or --importance &lt;importance level>)
 5. Delete a task:
 Delete &lt;task ID>
 6. Search for a task:
 search {[-N or --title &lt;title keyword>]|[-P or --progress {n(incomplete)| p(in progress)| c(complete)}]|[-D or --description &lt;description keyword>]|[-L or --location &lt;location>]|[-C or --category &lt;category name>]|[-T or --time {&lt;date+time>| &lt;&lt;starting date+time> to &lt;ending date+time>>}]|[-R or --recurring &lt;number h/d/w/m/y>]| [-I or --importance &lt;importance level>]}
 7. Search for an empty slot:
 searchempty &lt;&lt;starting date+time> to &lt;ending date+time>>
 8. Undo last changes:
 undo &lt;steps>
 9. List categories:
 listcategory
 10. Add a category:
 addcategory &lt;category name>
 11. Delete a category:
 delcategory &lt;category name>
 12. Display Help:
 help

