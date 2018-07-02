package com.softvision.model;

public enum EmployeeType {
    I,
    M;

    @Override
    public String toString() {
        return M + "," + I;
    }
}
