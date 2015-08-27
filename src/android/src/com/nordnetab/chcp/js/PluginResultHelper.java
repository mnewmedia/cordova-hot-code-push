package com.nordnetab.chcp.js;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nordnetab.chcp.config.ApplicationConfig;
import com.nordnetab.chcp.events.PluginEvent;

import org.apache.cordova.PluginResult;

/**
 * Created by Nikolay Demyankov on 29.07.15.
 *
 * Helper class to generate proper instance of PluginResult, which is send to the web side.
 *
 * @see PluginResult
 */
public class PluginResultHelper {

    // keywords for JSON string, that is send to JavaScript side
    private static class JsParams {
        private static class General {
            public static final String ACTION = "action";
            public static final String ERROR = "error";
            public static final String DATA = "data";
        }

        private static class UserInfo {
            public static final String CONFIG = "config";
        }

        private static class Error {
            public static final String CODE = "code";
            public static final String DESCRIPTION = "description";
        }
    }

    /**
     * Create PluginResult instance from event.
     *
     * @param event hot-code-push plugin event
     *
     * @return cordova's plugin result
     * @see PluginResult
     * @see PluginEvent
     */
    public static PluginResult pluginResultFromEvent(PluginEvent event) {
        JsonNode errorNode = null;
        JsonNode dataNode = null;

        if (event.error != null) {
            errorNode = createErrorNode(event.error.getErrorCode(), event.error.getErrorDescription());
        }

        if (event.config != null) {
            dataNode = createDataNode(event.config);
        }

        return getResult(event.eventName, dataNode, errorNode);
    }

    // region Private API

    private static JsonNode createDataNode(ApplicationConfig config) {
        JsonNodeFactory factory = JsonNodeFactory.instance;

        ObjectNode dataNode = factory.objectNode();
        dataNode.set(JsParams.UserInfo.CONFIG, factory.textNode(config.toString()));

        return dataNode;
    }

    private static JsonNode createErrorNode(int errorCode, String errorDescription) {
        JsonNodeFactory factory = JsonNodeFactory.instance;

        ObjectNode errorData = factory.objectNode();
        errorData.set(JsParams.Error.CODE, factory.numberNode(errorCode));
        errorData.set(JsParams.Error.DESCRIPTION, factory.textNode(errorDescription));

        return errorData;
    }

    private static PluginResult getResult(String action, JsonNode data, JsonNode error) {
        JsonNodeFactory factory = JsonNodeFactory.instance;

        ObjectNode resultObject = factory.objectNode();
        resultObject.set(JsParams.General.ACTION, factory.textNode(action));

        if (data != null) {
            resultObject.set(JsParams.General.DATA, data);
        }

        if (error != null) {
            resultObject.set(JsParams.General.ERROR, error);
        }

        return new PluginResult(PluginResult.Status.OK, resultObject.toString());
    }

    // endregion

}
