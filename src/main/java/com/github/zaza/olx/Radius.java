package com.github.zaza.olx;

enum Radius {
	_0KM, _2KM, _5KM, _10KM, _15KM, _30KM, _50KM, _75KM, _100KM;

	@Override
	public String toString() {
		return name().substring(1, name().length() - 2);
	}
}
