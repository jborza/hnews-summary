package com.jborza.hnews.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIResponse {

    private List<Output> output;

    @JsonProperty("output")
    public List<Output> getOutput() {
        return output;
    }

    @JsonProperty("output")
    public void setOutput(List<Output> output) {
        this.output = output;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        private List<Content> content;

        @JsonProperty("content")
        public List<Content> getContent() {
            return content;
        }

        @JsonProperty("content")
        public void setContent(List<Content> content) {
            this.content = content;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Content {
            private String text;

            @JsonProperty("text")
            public String getText() {
                return text;
            }

            @JsonProperty("text")
            public void setText(String text) {
                this.text = text;
            }
        }
    }
}