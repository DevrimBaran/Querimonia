/*package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResponseComponentManagerTest {

    private ResponseComponentManager responseComponentManager = new ResponseComponentManager();
    private TemplateRepository templateRepository;

    @Before
    public void setup() {
        TemplateRepository templateRepository = mock(TemplateRepository.class);
        when(templateRepository.save(any(ResponseComponent.class))).thenReturn(new ResponseComponent());
    }

    @Test
    public void testAddDefaultTemplates() {
        responseComponentManager.addDefaultTemplates(templateRepository);
    }
}*/
