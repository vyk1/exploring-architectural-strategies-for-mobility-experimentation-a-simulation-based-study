'use strict'

const fs = require('fs');

let targetFile = "file.csv"
const HEADER = "ID,Latitude,Longitude,Block,Level,Parent,State,Details\n"
const SEPARATOR = ","
const NEW_FILE_FILENAME = 'new.csv'

readCsvFile(targetFile)

function readCsvFile(filename) {
    fs.readFile(filename, 'utf8', function (err, data) {
        let dataArray = data.split(/\r?\n/)

        // removes header and last row
        dataArray.shift()
        dataArray.pop()

        let content = ""

        for (let i = 0; i < dataArray.length; i++) {
            let line = dataArray[i]
            let parenthesisIndex = line.indexOf(")")
            let ll = line.substring(0, parenthesisIndex).replace("(", "").replace("POINT ", "").replace(")", "").split(" ")

            let lat = ll[0]
            let lon = ll[1]
            let block = line.match(/(?<=(Ponto \d{1,}),).*/)[0]
            let details = "Block " + block + " Gateway"

            // EVERY NODE IS DEFINED AS STANDARD GATEWAY (LV=2, PARENT=1)
            content += i + SEPARATOR + lat + SEPARATOR + lon + SEPARATOR + block + SEPARATOR + "2" + SEPARATOR + "1" + SEPARATOR + "VIC" + SEPARATOR + details + "\n"
        }
        let str = HEADER + content


        fs.appendFileSync(NEW_FILE_FILENAME, str)
        console.log("wrote @ " + NEW_FILE_FILENAME)

    })
}
