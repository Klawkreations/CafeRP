package io.github.klawkreations.caferp.rp;

public enum ERole {
    OFFICER("Officer"), LOGGER("Logger"), MINER("Miner"), ENTREPRENEUR("Entrepreneur"), SCIENTIST("Scientist"),
    CRIMINAL("Criminal");

    private String title;

    ERole(String title) {
        this.title = title;
    }

    @Override
    public String toString(){
        return title;
    }
}
