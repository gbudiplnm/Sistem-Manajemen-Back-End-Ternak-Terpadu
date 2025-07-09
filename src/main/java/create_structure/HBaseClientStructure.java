package create_structure;

import com.github.javafaker.Faker;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class HBaseClientStructure {

        public static void main(String[] args) throws IOException {

                Configuration conf = HBaseConfiguration.create();
                conf.set("hbase.zookeeper.quorum", "192.168.56.101");
                HBaseCustomClient client = new HBaseCustomClient(conf);

                // ==============================================================================================
                // CREATE COLLECTION
                // ==============================================================================================

                // Create Tabel Berita6
                TableName tableBerita = TableName.valueOf("beritadev");
                String[] beritadev = { "main", "detail" };
                client.deleteTable(tableBerita);
                client.createTable(tableBerita, beritadev);

                // Create Tabel Hewan
                TableName tableHewan = TableName.valueOf("hewandev");
                String[] hewandev = { "main", "petugas", "peternak", "kandang", "jenisHewan", "rumpunHewan",
                                "tujuanPemeliharaan", "detail" };
                client.deleteTable(tableHewan);
                client.createTable(tableHewan, hewandev);

                // Create Tabel Jenis Hewan
                TableName tableJenisHewan = TableName.valueOf("jenishewandev");
                String[] jenishewandev = { "main", "detail" };
                client.deleteTable(tableJenisHewan);
                client.createTable(tableJenisHewan, jenishewandev);

                // Create Tabel Tujuan Pemeliharaan
                TableName tableTujuanPemeliharaan = TableName.valueOf("tujuanpemeliharaandev");
                String[] tujuanpemeliharaandev = { "main", "detail" };
                client.deleteTable(tableTujuanPemeliharaan);
                client.createTable(tableTujuanPemeliharaan, tujuanpemeliharaandev);

                TableName tableRumpunHewan = TableName.valueOf("rumpunhewandev");
                String[] rumpunhewandev = { "main", "detail" };
                client.deleteTable(tableRumpunHewan);
                client.createTable(tableRumpunHewan, rumpunhewandev);

                // Create Tabel Inseminasi
                TableName tableInseminasi = TableName.valueOf("inseminasidev");
                String[] inseminasidev = { "main", "peternak", "hewan", "petugas", "kandang", "jenisHewan",
                                "rumpunHewan", "detail" };
                client.deleteTable(tableInseminasi);
                client.createTable(tableInseminasi, inseminasidev);

                // Create Tabel Kandang
                TableName tableKandang = TableName.valueOf("kandangdev");
                String[] kandangdev = { "main", "jenisHewan", "peternak", "detail" };
                client.deleteTable(tableKandang);
                client.createTable(tableKandang, kandangdev);

                // Create Tabel Kelahiran
                TableName tableKelahiran = TableName.valueOf("kelahirandev");
                String[] kelahirandev = { "main", "peternak", "hewan", "jenisHewan", "rumpunHewan", "kandang",
                                "petugas", "inseminasi", "detail" };
                client.deleteTable(tableKelahiran);
                client.createTable(tableKelahiran, kelahirandev);

                // Create Tabel Pengobatan
                TableName tablePengobatan = TableName.valueOf("pengobatandev");
                String[] pengobatandev = { "main", "petugas", "detail" };
                client.deleteTable(tablePengobatan);
                client.createTable(tablePengobatan, pengobatandev);

                // Create Tabel Peternak
                TableName tablePeternak = TableName.valueOf("peternakdev");
                String[] peternakdev = { "main", "userdev", "petugas", "detail" };
                client.deleteTable(tablePeternak);
                client.createTable(tablePeternak, peternakdev);

                // Create Tabel Petugas
                TableName tablePetugas = TableName.valueOf("petugasdev");
                String[] petugasdev = { "main", "userdev", "detail" };
                client.deleteTable(tablePetugas);
                client.createTable(tablePetugas, petugasdev);

                // Create Tabel Pkb
                TableName tablePkb = TableName.valueOf("pkbdev");
                String[] pkbdev = { "main", "peternak", "hewan", "kandang", "petugas", "rumpunHewan", "jenisHewan",
                                "detail" };
                client.deleteTable(tablePkb);
                client.createTable(tablePkb, pkbdev);

                // Create Tabel Vaksin
                TableName tableVaksin = TableName.valueOf("vaksindev");
                String[] vaksindev = { "main", "peternak", "hewan", "petugas", "namaVaksin", "jenisVaksin", "detail" };
                client.deleteTable(tableVaksin);
                client.createTable(tableVaksin, vaksindev);

                // Create Tabel Jenis Vaksin
                TableName tableJenisVaksin = TableName.valueOf("jenisvaksindev");
                String[] jenisvaksindev = { "main", "detail" };
                client.deleteTable(tableJenisVaksin);
                client.createTable(tableJenisVaksin, jenisvaksindev);

                // Create Tabel Nama Vaksin
                TableName tableNamaVaksin = TableName.valueOf("namavaksindev");
                String[] namavaksindev = { "main", "jenisVaksin", "detail" };
                client.deleteTable(tableNamaVaksin);
                client.createTable(tableNamaVaksin, namavaksindev);

                // Create Tabel Penghijauan
                TableName tablePenghijauan = TableName.valueOf("penghijauandev");
                String[] penghijauandev = { "main", "detail" };
                client.deleteTable(tablePenghijauan);
                client.createTable(tablePenghijauan, penghijauandev);

                // Create Table Users
                TableName tableUser = TableName.valueOf("userdev");
                String[] userdev = { "main", "detail" };
                client.deleteTable(tableUser);
                client.createTable(tableUser, userdev);

                TableName tableLahanHijau = TableName.valueOf("lahanHijauDev");
                String[] lahanHijauDev = { "main","petugasInput","petugasReview","peternakInput","detail" };
                client.deleteTable(tableLahanHijau);
                client.createTable(tableLahanHijau, lahanHijauDev);

                TableName tablePuskesmas = TableName.valueOf("puskesmasDev");
                String[] puskesmasDev = { "main","petugasPencatat","detail" };
                client.deleteTable(tablePuskesmas);
                client.createTable(tablePuskesmas, puskesmasDev);

                TableName tablePasarTernak = TableName.valueOf("pasarTernakDev");
                String[] pasarTernakDev = { "main","petugasPencatat","detail" };
                client.deleteTable(tablePasarTernak);
                client.createTable(tablePasarTernak, pasarTernakDev);

                TableName tablePusatEvakuasi = TableName.valueOf("pusatEvakuasiDev");
                String[] pusatEvakuasiDev = { "main","petugasPencatat","detail" };
                client.deleteTable(tablePusatEvakuasi);
                client.createTable(tablePusatEvakuasi, pusatEvakuasiDev);

                // seeder
                // time now
                ZoneId zoneId = ZoneId.of("Asia/Jakarta");
                ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
                Instant instant = zonedDateTime.toInstant();

                // Insert Users
                client.insertRecord(tableUser, "USR001", "main", "id", "USR001");
                client.insertRecord(tableUser, "USR001", "main", "name", "Senja Abdi");
                client.insertRecord(tableUser, "USR001", "main", "username", "admin");
                client.insertRecord(tableUser, "USR001", "main", "email", "admin@gmail.com");
                client.insertRecord(tableUser, "USR001", "main", "password",
                                "$2a$10$SDRWMUk.2fnli0GTmqodJexjRksTw0En98dU8fdKsw7nTbZzMrj.2"); // password
                client.insertRecord(tableUser, "USR001", "main", "role", "1");
                // client.insertRecord(tableUser, "USR001", "main", "createdAt",
                // "2024-05-14T04:56:23.174Z");
                client.insertRecord(tableUser, "USR001", "detail", "createdAt", "2025-05-12T23:43:31.260Z");

                client.insertRecord(tableUser, "USR002", "main", "petugasId", "USR002");
                client.insertRecord(tableUser, "USR002", "main", "name", "petugas");
                client.insertRecord(tableUser, "USR002", "main", "username", "petugas");
                client.insertRecord(tableUser, "USR002", "main", "email", "petugas@gmail.com");
                client.insertRecord(tableUser, "USR002", "main", "password",
                                "$2a$10$SDRWMUk.2fnli0GTmqodJexjRksTw0En98dU8fdKsw7nTbZzMrj.2"); // password
                client.insertRecord(tableUser, "USR002", "main", "role", "2");
                // client.insertRecord(tableUser, "USR002", "main", "createdAt",
                // "2024-05-14T04:56:23.174Z");
                client.insertRecord(tableUser, "USR002", "detail", "createdAt", "2025-05-12T23:43:31.260Z");

                client.insertRecord(tableUser, "USR003", "main", "id", "USR003");
                client.insertRecord(tableUser, "USR003", "main", "name", "peternak");
                client.insertRecord(tableUser, "USR003", "main", "username", "peternak");
                client.insertRecord(tableUser, "USR003", "main", "email", "peternak@gmail.com");
                client.insertRecord(tableUser, "USR003", "main", "password",
                                "$2a$10$SDRWMUk.2fnli0GTmqodJexjRksTw0En98dU8fdKsw7nTbZzMrj.2"); // password
                client.insertRecord(tableUser, "USR003", "main", "role", "3");
                // client.insertRecord(tableUser, "USR003", "main", "createdAt",
                // "2024-05-14T04:56:23.174Z");
                client.insertRecord(tableUser, "USR003", "detail", "createdAt", "2025-05-12T23:43:31.260Z");

        }
}