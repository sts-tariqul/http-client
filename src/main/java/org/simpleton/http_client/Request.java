/**
 * 
 */
package org.simpleton.http_client;

/**
 * 
 * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * CreatedAt : Apr 30, 2023 12:39:22 AM
 * 
 *  GET: Retrieve data from the server. It should not have any side effects on the server.

    POST: Submit data to the server to create a new resource. It can also be used to update existing resources if the server supports it.

    PUT: Update an existing resource on the server. It replaces the resource with the provided data.

    DELETE: Remove a resource from the server.

    HEAD: Retrieve the headers of a resource without the actual content. It's often used to check if a resource has changed.

    PATCH: Apply partial modifications to a resource on the server. It's used for making partial updates.

    OPTIONS: Retrieve information about the communication options for the target resource. It's often used to determine which HTTP methods are supported by the server.

    CONNECT: Establish a network connection to the target resource. It's typically used for tunneling purposes, such as with HTTPS.

    TRACE: Retrieve a diagnostic trace of the request and response messages for a resource. It's primarily used for debugging purposes.
 *
 */
interface Request {
	
	public APIResponse get();
	
	public APIResponse post();
	
	public APIResponse put();
	
	public APIResponse delete();
	
	public APIResponse head();
	
	public APIResponse patch();
	
	public APIResponse options();
	
	public APIResponse trace();

}
