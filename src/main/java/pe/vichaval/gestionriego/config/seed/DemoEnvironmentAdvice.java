package pe.vichaval.gestionriego.config.seed;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@Profile("demo")
public class DemoEnvironmentAdvice {
    @ModelAttribute("entornoDemo")
    public boolean entornoDemo() {
        return true;
    }
}
