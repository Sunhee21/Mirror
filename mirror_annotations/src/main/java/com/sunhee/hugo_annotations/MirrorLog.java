package com.sunhee.hugo_annotations;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target(METHOD)
public @interface MirrorLog {
}
