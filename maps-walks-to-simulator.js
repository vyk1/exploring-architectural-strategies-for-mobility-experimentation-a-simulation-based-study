'use strict'

const fs = require('fs');

let targetFile = "walk.csv"

const HEADER = "Latitude,Longitude\n"
const SEPARATOR = ","
const NEW_FILE_FILENAME = 'new-walk.csv'

readCsvFile(targetFile)

function readCsvFile(filename) {
    fs.readFile(filename, 'utf8', function (err, data) {

        let dataArray = data.split(/\r?\n/)
        // Removes header 
        dataArray.shift()
        // Removes last row 
        dataArray.pop()

        let content = ""

        for (let i = 0; i < dataArray.length; i++) {
            let line = dataArray[i]
            let ll = line.match(/(?<=(POINT \()).*(?=\))/)[0]
            ll.replace(")", "")
            let [lat, lon] = ll.split(" ")

            content += lat + SEPARATOR + lon + "\n"
        }

        let str = HEADER + content.toString()

        fs.appendFileSync(NEW_FILE_FILENAME, str)
        console.log("wrote @ " + NEW_FILE_FILENAME)
    })
}