package jk.querynex.notify

class FakeNotifier implements INotifier {

	public void send(String msg) {
		//just show msg on console
		println msg
	}
}
