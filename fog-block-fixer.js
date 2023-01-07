/* 
File to fix gateways' parent_id to match their block 
*/
'use strict'

const fs = require('fs');

let targetFile = "test.csv"

const SEPARATOR = ","
const NEW_FILE_FILENAME = 'fixed.csv'
// const NEW_FILE_FILENAME = 'fixed-' + new Date().valueOf() + '.csv'

const GATEWAY_ID_LEVEL = "2"

readCsvFile(targetFile)

function readCsvFile(filename) {
    fs.readFile(filename, 'utf8', function (err, data) {
        let dataArray = data.split(/\r?\n/)

        let header = dataArray[0]
        // Removes header 
        dataArray.shift()
        // Removes last row
        dataArray.pop()

        let content = ""

        for (let i = 0; i < dataArray.length; i++) {

            let [id, lat, lon, block, lvl, parent, state, details] = dataArray[i].split(SEPARATOR)

            if (lvl === GATEWAY_ID_LEVEL) {
                // For the gateways, the parent level should be the same as the corresponding proxy (aka block id)
                content += id + SEPARATOR + lat + SEPARATOR + lon + SEPARATOR + block + SEPARATOR + GATEWAY_ID_LEVEL + SEPARATOR + block + SEPARATOR + state + SEPARATOR + details + "\n"
            } else {
                content += dataArray[i] + "\n"
            }
        }

        let str = header + '\n' + content

        fs.appendFileSync(NEW_FILE_FILENAME, str)

        console.log("Written @ " + NEW_FILE_FILENAME)
    })
}