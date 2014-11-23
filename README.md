SomVis
======

Self-Organizing Map Visualizer


## Sample Usage

train a SOM with some input data:
`java -jar target/somvis-1.0-jar-with-dependencies.jar -c src/main/resources/clus10.config.json -t src/main/resources/clus10.input.jsonrows -o src/main/resources/clus10.data.jsonrows`

view the input data:
`java -cp target/somvis-1.0-jar-with-dependencies.jar com.github.kimmokarlsson.somvis.som.ui.SomVisualizer src/main/resources/clus10.config.json src/main/resources/clus10.data.jsonrows`
