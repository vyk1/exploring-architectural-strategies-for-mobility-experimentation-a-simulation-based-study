'use strict'

//requiring path and fs modules
const path = require('path');
const fs = require('fs');
//joining path of directory 
//const directoryPath = path.join(__dirname, dirPath);
const directoryPath = "25-users/clustered"
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
		console.log("reading" + fullPath)
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
	let variance = sum / arr.length

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
			aldArr.push(Number(dataArray[i].split(";")[0].replace(",", ".")))
			cicArr.push(Number(dataArray[i].split(";")[1].replace(",", ".")))
			nuArr.push(Number(dataArray[i].split(";")[2].replace(",", ".")))
			mtArr.push(Number(dataArray[i].split(";")[3].replace(",", ".")))
		}

		let devA = dev(aldArr)
		let devC = dev(cicArr)
		let devN = dev(nuArr)
		let devM = dev(mtArr)

		let str = filename + " " + devA[0] + " " + devA[1] + devC[0] + " " + devC[1] + devN[0] + " " + devN[1] + devM[0] + " " + devM[1] + "\n"

		fs.appendFileSync("output-std-cl.csv", str)

	});
}
