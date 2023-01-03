The files are written in the `results` folder using semicolon as separator and dot as floating point indicator because of my version of GNUPlot.

Example:

```
ALD;COST IN CLOUD;NU;MT
106.0619845344754;30793.079999995694;33133.94;14.399999999999869
```

For Google Drive, your file should be:

```
ALD;COST IN CLOUD;NU;MT
106,0619845344754;30793,079999995694;33133,94;14,399999999999869
```

When importing, don't forget to:

1. Go to File > Import
2. Select your file
3. Select 'Replace the selected cell with data'
4. Select Custom file separator
5. Write ;
6. Uncheck the automatic file data type conversion
7. Import it
8. Don't forget to convert the numbers