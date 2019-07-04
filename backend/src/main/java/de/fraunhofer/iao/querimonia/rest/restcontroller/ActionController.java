package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.ActionRepository;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.rest.manager.ActionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

// TODO documentation/implementation

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ActionController {

  private final ActionManager actionManager;

  public ActionController(ActionRepository actionRepository) {
    this.actionManager = new ActionManager(actionRepository);
  }

  @GetMapping("api/actions")
  public ResponseEntity<List<Action>> getActions(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("action_code") Optional<String> actionCode,
      @RequestParam("keywords") Optional<String[]> keywords) {

    return new ResponseEntity<>
        (actionManager.getActions(count, page, sortBy, keywords), HttpStatus.OK);
  }

  @PostMapping("api/actions")
  public ResponseEntity<Action> addAction(@RequestBody Action action) {

    return new ResponseEntity<>(actionManager.addAction(action), HttpStatus.OK);
  }

  @GetMapping("api/actions/{actionId}")
  public ResponseEntity<Action> getAction(@PathVariable int actionId) {
    Optional<Action> possibleAction = actionManager.getActionbyId(actionId);
    return possibleAction
        .map(action -> new ResponseEntity<>(action, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

  }

  @DeleteMapping("api/actions/{actionId}")
  public ResponseEntity deleteAction(@PathVariable int actionId) {
    return actionManager.deleteAction(actionId)?
        new ResponseEntity(HttpStatus.NO_CONTENT):
        new ResponseEntity(HttpStatus.NOT_FOUND);
  }

  @PutMapping("api/actions/{actionId}")
  public ResponseEntity<Action> updateAction(@PathVariable int actionId,
                                             @RequestBody Action newAction) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @GetMapping("api/actions/count")
  public ResponseEntity<Integer> countActions() {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @DeleteMapping("api/actions/all")
  public ResponseEntity deleteAllActions(@PathVariable int actionId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
