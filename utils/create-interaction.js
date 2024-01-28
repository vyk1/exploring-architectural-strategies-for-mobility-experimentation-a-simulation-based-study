/**
 * @author Victoria Botelho Martins
 * 
 * Creates user interaction given a file
 * 
 */

'use strict'

const fs = require('fs')

// The total lines in the .csv file = USERS * INTERECTIONS PER USER

const HEADER = 'Latitude,Longitude,Interaction\n'

const SEPARATOR = ','

const CSV_FILE = `merged.csv`

// Each user trailed x interactions containing <lat, lng>
const INTERACTIONS = 20

fs.readFile(CSV_FILE, 'utf8', function (err, data) {
    const DATA = data.split(/\r?\n/)

    let content = ''

    // Removes header and last row
    DATA.shift()
    DATA.pop()

    for (let index = 0; index < DATA.length; index++) {
        content += DATA[index] + SEPARATOR + (index % INTERACTIONS + 1) + '\n'
    }

    content = content.replace(/^/, HEADER)
    let filename = 'with-iteractions.csv'
    fs.appendFileSync(filename, content)

    console.log("Written @" + filename);

})