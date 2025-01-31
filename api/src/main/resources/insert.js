// ######## Compatability mode for java 8. ##########
try {
   load("nashorn:mozilla_compat.js");
} catch (exception){}

/**
 * prints the line to the output. will be log
 * 
 * @param line
 *            the line to print
 */
function logWithDate(line) {
	ioBean.println(new Date().toString + ": " + line);
}

/**
 * prints the line in info context.
 * 
 * @param line
 *            the line to print
 */
function log(line) {
	ioBean.println(line);
}
/**
 * prints the line in error context.
 * 
 * @param line
 *            the line to print
 */
function error(line) {
	ioBean.error(line);
}

/**
 * prints the line in debug context. Will be logged to console or test but not
 * to logs when in production.
 * 
 * @param line
 *            the line to print
 */
function debug(line) {
	ioBean.debug(line);
}

//######## Data Transformational functions ##########

function toJsonObj(text) {
	try {
		return JSON.parse(text);
	} catch(e) {
		error("Error parsing json: " + e);
	}
	return text;
}

function toJsonString(jsonObj) {
	try {
		return JSON.stringify(jsonObj, null, '\t');
	} catch(e) {
		error("Error converting json to string json: " + e);
	}
	return jsonObj;
}


//######## Data Retrieval functions ##########
/**
 * gets the com.intuit.tank.http.BaseRequest object of the last call made.
 * 
 * @return the request or null if no requests have been made
 */
function getRequest() {
	return ioBean.getInput("request");
}

/**
 * gets the com.intuit.tank.http.BaseResponse object of the last call made.
 * 
 * @return the response or null if no requests have been made
 * 
 */
function getResponse() {
	return ioBean.getInput("response");
}

/**
 * gets the request body as string if not binary
 * 
 * @return the body or empty string if null or binary
 */
function getResquestBody() {
	if (getRequest() != null) {
		return getRequest().getBody();
	}
	return "";
}

/**
 * gets the response body as string if not binary
 * 
 * @return the body or empty string if null or binary
 */
function getResponseBody() {
	if (getResponse() != null) {
	return getResponse().getBody();
	}
	return "";
}

/**
 * Returns the value of the variable or null if variable does not exist
 * 
 * @param key
 *            the variable name
 * @returns the variable value.
 */
function getVariable(key) {
	return ioBean.getInput("variables").getVariable(key);
}

/**
 * Sets the value of the variable.
 * 
 * @param key
 *            the variable name to set
 * @param value
 *            the value to set it to
 */
function setVariable(key, value) {
	ioBean.getInput("variables").addVariable(key, value);
}

//######## Result functions ##########

/**
 * jump to a group within the script with the specified name.
 * 
 * @param groupName
 *            the name of the group to go to. if the group does not exist\, it
 *            skips to next script.
 */
function gotoGroup(groupName) {
	setAction("goto " + groupName);
}

/**
 * restart the TestPlan from the start.
 */
function restartPlan() {
	setAction("restartPlan");
}

/**
 * aborts the current ScriptGroup and proceeds to the next.
 * 
 * @returns
 */
function abortScriptGroup() {
	setAction("abortScriptGroup");
}

/**
 * aborts the current Script and proceeds to the next.
 */
function abortScript() {
	setAction("abortScript");
}

/**
 * aborts the current group and proceeds to the next request after the current
 * group.
 */
function abortGroup() {
	setAction("abortGroup");
}

/**
 * terminates this user.
 */
function terminateUser() {
	setAction("terminateUser");
}

/**
 * sets the action in the output.
 */
function setAction(action) {
	ioBean.setOutput("action", action);
}


