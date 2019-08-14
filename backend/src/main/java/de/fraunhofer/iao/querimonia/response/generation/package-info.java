/**
 * This package is used for response generation. Responses are generated with a {@link
 * de.fraunhofer.iao.querimonia.response.generation.ResponseGenerator}. Each complaint has
 * exactly one {@link de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion response}.
 * A response consists of a list of {@link de.fraunhofer.iao.querimonia.response.action.Action
 * actions} that are executed on when a complaint gets closed and a list of
 * {@link de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent completed
 * response components}, which represent the textual answer. These completed components
 * consist of a {@link de.fraunhofer.iao.querimonia.response.generation.ResponseComponent} and a
 * list of entities, which are used to fill the placeholders used in the components.
 */

package de.fraunhofer.iao.querimonia.response.generation;