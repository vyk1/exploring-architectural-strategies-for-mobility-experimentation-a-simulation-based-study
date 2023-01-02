'use strict'

const fs = require('fs');

let targetFile = "sample.csv"

const HEADER = "ID,Latitude,Longitude,Block,Level,Parent,State,Details\n"
const CLOUD = "0,-37.8136,144.9631,0,0,-1,VIC,Cloud Data Center\n"
const SEPARATOR = ","
const NEW_FILE_FILENAME = 'converted.csv'

const GATEWAY_ID_LEVEL = "2"
const PROXY_ID_LEVEL = "1"
const CLOUD_ID_LEVEL = "0"

readCsvFile(targetFile)

function readCsvFile(filename) {
    fs.readFile(filename, 'utf8', function (err, data) {
        let dataArray = data.split(/\r?\n/)

        // Removes header 
        dataArray.shift()
        // Removes last row
        dataArray.pop()

        let content = ""

        let array = []

        for (let i = 0; i < dataArray.length; i++) {
            let line = dataArray[i]
            let parenthesisIndex = line.indexOf(")")
            let ll = line.substring(1, parenthesisIndex).replace("(", "").replace("POINT ", "").replace(")", "").split(" ")

            let [lon, lat] = ll

            let block = line.match(/(?<=(Ponto \d{1,}),).*/)[0]
            let details = "Block " + block + " Gateway"

            content = lat + SEPARATOR + lon + SEPARATOR + block + SEPARATOR + GATEWAY_ID_LEVEL + SEPARATOR + PROXY_ID_LEVEL + SEPARATOR + "VIC" + SEPARATOR + details + "\n"
            array[block] == null ? array[block] = [content] : array[block].push(content)
        }

        let everything = createProxies(array)

        let str = HEADER + CLOUD + everything.toString()

        fs.appendFileSync(NEW_FILE_FILENAME, str)
        console.log("wrote @ " + NEW_FILE_FILENAME)
    })
}

function createProxies(array) {
    let str = ""
    let copy = array
    // 1 bc blocks
    let counter = array.length - 1
    for (let index = 1; index < array.length; index++) {
        const group = array[index]
        let first = group[0]

        let blockId = first.split(",")[2]
        let lat = first.split(",")[0]
        let lon = first.split(",")[1]

        let details = "Block " + blockId + " Proxy"

        let proxy = blockId + SEPARATOR + lat + SEPARATOR + lon + SEPARATOR + blockId + SEPARATOR + PROXY_ID_LEVEL + SEPARATOR + CLOUD_ID_LEVEL + SEPARATOR + "VIC" + SEPARATOR + details + "\n"
        copy[index][0] = proxy

        str += proxy

        for (let jindex = 1; jindex < group.length; jindex++) {
            counter++
            const node = group[jindex];
            let gateway = (counter) + SEPARATOR + node

            str += gateway
            copy[index][jindex] = gateway
        }
    }

    return str
}