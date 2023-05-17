package org.ecnusmartboys.domain.entity;

public class Visitor extends User{
    private String emergencyContactName;
    private String emergencyContactPhone;
    public static class Builder extends User.Builder{
        protected final Visitor product;
        public Builder() {
            super(new Visitor());
            this.product = (Visitor) super.product;
        }
    }
}
