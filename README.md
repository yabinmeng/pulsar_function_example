# Decription

This is a simple Pulsar function example that showcases the high level workflow of how to develop and deploy a function in a Pulsar cluster.

In this simple function, it does the following processing for each message of the input topic:
1) Log a message of each received message
2) Add a message property to each received message
3) Generate a new message with payload slightly modified from the input message payload 
4) Send the new message to an output topic

The high level workflow of deploying the function in a Pulsar cluster is as below

1) Build an Uber jar
```
$ gradle clean build shadowJar
```

2) Start a Pulsar cluster and enable function worker as part of the broker via the following setting in broker.conf
```
functionsWorkerEnabled=true
```

3) Copy the Uber jar to Pulsar broker nodes

4) Register the function in the Pulsar cluster. An example is as below. In this example, a Pulsar function named "AddMetadata" is registered. It will process each message from the topic "persistent://public/default/t1" and send a new message to another topic "persistent://public/default/t1_meta"
```
$ pulsar-admin functions create \
  --name AddMetadata \
  --jar /home/johndoe/pulsar_function_example-1.0-SNAPSHOT-all.jar \
  --classname com.example.AddMetadataFunc \
  --auto-ack true \
  --inputs persistent://public/default/t1 \
  --output persistent://public/default/t1_meta \
  --log-topic persistent://public/default/t1_log
```