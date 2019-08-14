/**
 * Querimonia is a software to analyze complaint texts and generate responses to them.
 *
 * <h1>The complaint life cycle</h1>
 *
 * <p>New complaints can be added via http. Http requests are manged in the
 * {@link de.fraunhofer.iao.querimonia.rest rest}-package, complaint requests are accepted in
 * the {@link de.fraunhofer.iao.querimonia.rest.restcontroller.ComplaintController complaint
 * controller}. Complaints can also be edited and deleted.</p>
 *
 * <p>All requests for creating/editing/deleting complaints are managed in the
 * {@link de.fraunhofer.iao.querimonia.manager manager}-package. This uses the connection to
 * the database in the {@link de.fraunhofer.iao.querimonia.repository repository}-package.</p>
 *
 * <p>Complaint objects get created in the {@link de.fraunhofer.iao.querimonia.complaint
 * complaint}-package. The complaint text gets analyzed with different tools, which are
 * implemented in the {@link de.fraunhofer.iao.querimonia.nlp nlp}-package. This includes
 * sentiment analysis, classification of the text, named entity extraction. Which tools are used
 * for the analysis can be defined using {@link de.fraunhofer.iao.querimonia.config configurations}.
 * </p>
 *
 * <p>After the analysis, a response to the complaint is generated in the
 * {@link de.fraunhofer.iao.querimonia.response response}-package.</p>
 */

package de.fraunhofer.iao.querimonia;