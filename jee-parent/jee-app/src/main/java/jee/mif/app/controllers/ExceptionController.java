package jee.mif.app.controllers;

import jee.mif.bl.exceptions.VersionNotLatestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Tadas.
 */
@ControllerAdvice
public class ExceptionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private static final String PATH = "/error";
    private static final String message = "Data version is not latest. Reload data and continue your work.";

    @ExceptionHandler({VersionNotLatestException.class, ObjectOptimisticLockingFailureException.class})
    public ModelAndView versionNotLatestException(Exception e) {
        logger.error("Error happened: ", e);
        ModelAndView model = new ModelAndView(PATH);
        model.addObject("message", message);
        return model;
    }

    @ExceptionHandler(Exception.class)
    public String handleAllException(Exception e) {
        logger.error("Error happened: ", e);
        return PATH;
    }
}

