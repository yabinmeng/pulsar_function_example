package com.example;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AddMetadataFunc implements Function<String, Void> {
    @Override
    public Void process(String input, Context context) throws Exception {

        Logger LOG = context.getLogger();
        String outputTopic = context.getOutputTopic();

        String output = String.format("%s!!!", input);

        String inputTopics = context.getInputTopics().stream().collect(Collectors.joining(", "));
        String logMessage = String.format("A message with a value of \"%s\" has arrived on one of the following topics: %s\n",
                input,
                inputTopics);
        LOG.info(logMessage);

        try {
            TypedMessageBuilder messageBuilder
                    = context.newOutputMessage(outputTopic, Schema.STRING);
            messageBuilder = messageBuilder
                    .property("Prop1", "This is a test!")
                    .value(output);

            if (context.getCurrentRecord().getKey().isPresent()) {
                messageBuilder.key(context.getCurrentRecord().getKey().get());
            }
            messageBuilder.eventTime(System.currentTimeMillis()).sendAsync();
        }
        catch (PulsarClientException e) {
            LOG.error(e.toString());
        }

        return null;
    }
}
