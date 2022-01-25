package hanze.nl.bussimulator;

import com.thoughtworks.xstream.XStream;

public class BusTransmitter {
    public void sendETAs(Bus bus, int nu ){
        Lijnen lijn = bus.getLijn();
        Bedrijven bedrijf = bus.getBedrijf();
        String busID = bus.getBusID();
        boolean bijHalte = bus.isBijHalte();
        int halteNummer = bus.getHalteNummer();
        int richting = bus.getRichting();
        int totVolgendeHalte = bus.getTotVolgendeHalte();

        int i=0;
        Bericht bericht = new Bericht(lijn.name(),bedrijf.name(),busID,nu);
        if (bijHalte) {
            ETA eta = new ETA(lijn.getHalte(halteNummer).name(),lijn.getRichting(halteNummer),0);
            bericht.ETAs.add(eta);
        }
        Halte.Positie eerstVolgende=lijn.getHalte(halteNummer+richting).getPositie();
        int tijdNaarHalte=totVolgendeHalte+nu;
        for (i = halteNummer+richting ; !(i>=lijn.getLengte()) && !(i < 0); i=i+richting ){
            tijdNaarHalte+= lijn.getHalte(i).afstand(eerstVolgende);
            ETA eta = new ETA(lijn.getHalte(i).name(), lijn.getRichting(i),tijdNaarHalte);
            bericht.ETAs.add(eta);
            eerstVolgende=lijn.getHalte(i).getPositie();
        }
        bericht.eindpunt=lijn.getHalte(i-richting).name();
        sendBericht(bericht);
    }

    public void sendLastETA(Bus bus, int nu){
        Lijnen lijn = bus.getLijn();
        Bedrijven bedrijf = bus.getBedrijf();
        String busID = bus.getBusID();
        int halteNummer = bus.getHalteNummer();

        Bericht bericht = new Bericht(lijn.name(),bedrijf.name(),busID,nu);
        String eindpunt = lijn.getHalte(halteNummer).name();
        ETA eta = new ETA(eindpunt,lijn.getRichting(halteNummer),0);
        bericht.ETAs.add(eta);
        bericht.eindpunt = eindpunt;
        sendBericht(bericht);
    }

    public void sendBericht(Bericht bericht){
        XStream xstream = new XStream();
        xstream.alias("Bericht", Bericht.class);
        xstream.alias("ETA", ETA.class);
        String xml = xstream.toXML(bericht);
        Producer producer = new Producer();
        producer.sendBericht(xml);
    }
}
