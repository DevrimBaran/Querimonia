package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.db.repositories.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResponseComponentManagerTest {

    private ResponseComponentManager responseComponentManager = new ResponseComponentManager();
    private ResponseComponentRepository templateRepository;

    @Before
    public void setup() {
        templateRepository = mock(ResponseComponentRepository.class);
        when(templateRepository.save(any(ResponseComponent.class))).thenReturn(new ResponseComponent());
    }

    @Test
    public void testAddDefaultTemplates() {
        // TODO fix
        // responseComponentManager.addDefaultTemplates(templateRepository);
    }
}
