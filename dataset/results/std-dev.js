'use strict'

//requiring path and fs modules
const path = require('path');
const fs = require('fs');
//joining path of directory 
//const directoryPath = path.join(__dirname, dirPath);
const experimentEnv = "ens"
const userQtd = "5-users"
const experimentType = "clustered"

const directoryPath = experimentEnv + "/" + userQtd + "/" + experimentType
//const directoryPath = "./25-users/edgeward"
//passsing directoryPath and callback function
fs.readdir(directoryPath, function(err, files) {
	//handling error
	if (err) {
		return console.log('Unable to scan directory: ' + err);
	}
	//listing all files using forEach
	files.forEach(function(file) {
		let fullPath = directoryPath + "/" + file
		console.log("reading " + fullPath)
		readCsvFile(fullPath)
	});
});

function twoDecimal(num) {
	return Math.round((num + Number.EPSILON) * 100) / 100
	//return num
}

function dev(arr) {
	// Creating the mean with Array.reduce
	let mean = arr.reduce((acc, curr) => {
		return acc + curr
	}, 0) / arr.length;

	// Assigning (value - mean) ^ 2 to every array item
	arr = arr.map((k) => {
		return (k - mean) ** 2
	})

	// Calculating the sum of updated array
	let sum = arr.reduce((acc, curr) => acc + curr, 0);

	// Calculating the variance
	// let variance = sum / arr.length

	// Returning the Standered deviation
	return [twoDecimal(mean), twoDecimal(Math.sqrt(sum / arr.length))]
}

function readCsvFile(filename) {
	fs.readFile(filename, 'utf8', function(err, data) {
		let dataArray = data.split(/\r?\n/);

		//removes header and last row
		dataArray.shift();
		dataArray.pop();

		// format: ALD;CIC;NU;MT

		let aldArr = []
		let cicArr = []
		let nuArr = []
		let mtArr = []

		for (let i = 0; i < dataArray.length; i++) {
			aldArr.push(twoDecimal(Number(dataArray[i].split(";")[0])))
			cicArr.push(twoDecimal(Number(dataArray[i].split(";")[1])))
			nuArr.push(twoDecimal(Number(dataArray[i].split(";")[2])))
			mtArr.push(twoDecimal(Number(dataArray[i].split(";")[3])))
		}

		let devA = dev(aldArr)
		let devC = dev(cicArr)
		let devN = dev(nuArr)
		let devM = dev(mtArr)

		//let header = "filename ald aldstd cic cicstd nu nustd mt mtstd\n"
		let content = filename + " " + devA[0] + " " + devA[1] + " " + devC[0] + " " + devC[1] + " " + devN[0] + " " + devN[1] + " " + devM[0] + " " + devM[1] + "\n"

		let str = content

		let csvFilename = directoryPath + ".csv"
		fs.appendFileSync(csvFilename, str)
		console.log("wrote @ " + csvFilename)

	});
}
