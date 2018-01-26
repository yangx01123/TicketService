package com.app;

public class Seat {
    private int _id;
    private String _confirmCode;
    private String _customerEmail;

    public Seat(int id) {
        this._id = id;
    }

    public int get_id() {
        return _id;
    }

    public String get_confirmCode() {
        return _confirmCode;
    }

    public void set_confirmCode(String _confirmCode) {
        this._confirmCode = _confirmCode;
    }

    public String get_customerEmail() {
        return _customerEmail;
    }

    public void set_customerEmail(String _customerEmail) {
        this._customerEmail = _customerEmail;
    }

    @Override
    public String toString() {
        return String.format("[SeatId: %s, ConfirmCode: %s, CustomerEmail: %s]", get_id(), get_confirmCode(), get_customerEmail());
    }
}

